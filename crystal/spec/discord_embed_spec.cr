require "spec"
require "../src/medusae"

describe Medusae::Client::DiscordEmbed do
  it "includes image and thumbnail when provided" do
    embed = Medusae::Client::DiscordEmbed.new("Title", "Description", 0xFFFFFF)
      .with_url("https://example.com")
      .with_image("https://example.com/image.png")
      .with_thumbnail("https://example.com/thumb.png")

    payload = embed.to_payload

    payload["title"].as_s.should eq("Title")
    payload["description"].as_s.should eq("Description")
    payload["color"].as_i.should eq(0xFFFFFF)
    payload["url"].as_s.should eq("https://example.com")
    payload["image"]["url"].as_s.should eq("https://example.com/image.png")
    payload["thumbnail"]["url"].as_s.should eq("https://example.com/thumb.png")
  end

  it "skips blank text fields" do
    embed = Medusae::Client::DiscordEmbed.new(" ", "", nil)
      .with_image(" ")
      .with_thumbnail(nil)
      .with_url(" ")

    payload = embed.to_payload
    payload.empty?.should be_true
  end
end
