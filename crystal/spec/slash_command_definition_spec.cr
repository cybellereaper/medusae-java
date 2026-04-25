require "spec"
require "../src/medusae"

describe Medusae::Client::SlashCommandDefinition do
  it "builds payload with options" do
    command = Medusae::Client::SlashCommandDefinition.new(
      "echo",
      "Echo text",
      [Medusae::Client::SlashCommandOptionDefinition.autocompleted_string("text", "Text to echo", true)]
    )

    payload = command.to_request_payload
    payload["type"].as_i.should eq(1)
    payload["name"].as_s.should eq("echo")
    payload["options"].as_a.size.should eq(1)
  end

  it "builds context menu payload without description and options" do
    payload = Medusae::Client::SlashCommandDefinition.user_context_menu("Inspect User")
      .with_dm_permission(false)
      .with_nsfw(false)
      .to_request_payload

    payload["type"].as_i.should eq(2)
    payload.has_key?("description").should be_false
    payload.has_key?("options").should be_false
    payload["dm_permission"].as_bool.should be_false
  end

  it "supports default member permissions" do
    command = Medusae::Client::SlashCommandDefinition.simple("secure", "Secure command")
      .with_default_member_permissions(Medusae::Client::DiscordPermissions.of(
        Medusae::Client::DiscordPermissions::SEND_MESSAGES,
        Medusae::Client::DiscordPermissions::USE_APPLICATION_COMMANDS
      ))

    command.to_request_payload.has_key?("default_member_permissions").should be_true
  end

  it "validates required fields" do
    expect_raises(ArgumentError) { Medusae::Client::SlashCommandDefinition.simple("", "desc") }
    expect_raises(ArgumentError) { Medusae::Client::SlashCommandDefinition.simple("ping", "") }
    expect_raises(ArgumentError) { Medusae::Client::SlashCommandOptionDefinition.string("", "text", true) }
    expect_raises(ArgumentError) do
      Medusae::Client::SlashCommandDefinition.new(
        Medusae::Client::SlashCommandDefinition::USER,
        "menu",
        "desc"
      )
    end
  end

  it "supports localizations and contexts in payload" do
    payload = Medusae::Client::SlashCommandDefinition.simple("settings", "Settings")
      .with_name_localizations({"en-US" => "Settings"})
      .with_description_localizations({"en-US" => "Manage settings"})
      .with_contexts([0, 2])
      .to_request_payload

    payload["name_localizations"]["en-US"].as_s.should eq("Settings")
    payload["description_localizations"]["en-US"].as_s.should eq("Manage settings")
    payload["contexts"].as_a.map(&.as_i).should eq([0, 2])
  end
end
