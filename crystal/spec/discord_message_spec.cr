require "spec"
require "../src/medusae"

describe Medusae::Client::DiscordMessage do
  it "includes ephemeral flag for ephemeral responses" do
    message = Medusae::Client::DiscordMessage.of_content("hidden").as_ephemeral
    payload = message.to_payload

    payload["content"].as_s.should eq("hidden")
    payload["flags"].as_i.should eq(64)
  end

  it "filters null and empty embeds" do
    message = Medusae::Client::DiscordMessage.new(
      "hello",
      embeds: [nil, Medusae::Client::DiscordEmbed.new("Title", nil, nil), Medusae::Client::DiscordEmbed.new(" ", "", nil)]
    )

    payload = message.to_payload
    embeds = payload["embeds"].as_a

    embeds.size.should eq(1)
    embeds.first["title"].as_s.should eq("Title")
  end

  it "includes allowed mentions and message reference when configured" do
    mentions = {"parse" => Medusae::Support::JsonPayload.any(["users"])}
    reference = {
      "message_id" => Medusae::Support::JsonPayload.any("123"),
      "fail_if_not_exists" => Medusae::Support::JsonPayload.any(false),
    }

    message = Medusae::Client::DiscordMessage.of_content("reply")
      .with_allowed_mentions(mentions)
      .with_message_reference(reference)

    payload = message.to_payload
    payload["allowed_mentions"]["parse"].as_a.first.as_s.should eq("users")
    payload["message_reference"]["message_id"].as_s.should eq("123")
  end
end
