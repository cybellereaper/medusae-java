require "spec"
require "../src/medusae"

describe Medusae::Client::DiscordMessage do
  it "includes ephemeral flag when ephemeral" do
    message = Medusae::Client::DiscordMessage.of_content("hidden").as_ephemeral
    payload = message.to_payload

    payload["content"].as_s.should eq("hidden")
    payload["flags"].as_i.should eq(64)
  end

  it "filters null and empty embeds" do
    message = Medusae::Client::DiscordMessage.new(
      "hello",
      [nil, Medusae::Client::DiscordEmbed.new("Title", nil, nil), Medusae::Client::DiscordEmbed.new(" ", "", nil)]
    )

    payload = message.to_payload
    payload["content"].as_s.should eq("hello")
    embeds = payload["embeds"].as_a
    embeds.size.should eq(1)
    embeds[0]["title"].as_s.should eq("Title")
  end

  it "includes allowed mentions and message reference when configured" do
    message = Medusae::Client::DiscordMessage.of_content("reply")
      .with_allowed_mentions({"parse" => Medusae::Client::PayloadSupport.any(["users"])})
      .with_message_reference({
        "message_id" => Medusae::Client::PayloadSupport.any("123"),
        "fail_if_not_exists" => Medusae::Client::PayloadSupport.any(false),
      })

    payload = message.to_payload
    payload["allowed_mentions"]["parse"].as_a[0].as_s.should eq("users")
    payload["message_reference"]["message_id"].as_s.should eq("123")
  end
end
