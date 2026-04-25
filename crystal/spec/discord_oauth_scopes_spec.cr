require "spec"
require "../src/medusae"

describe Medusae::Client::DiscordOAuthScopes do
  it "joins scopes in order and deduplicates" do
    scopes = Medusae::Client::DiscordOAuthScopes.join(
      Medusae::Client::DiscordOAuthScopes::BOT,
      Medusae::Client::DiscordOAuthScopes::APPLICATIONS_COMMANDS,
      Medusae::Client::DiscordOAuthScopes::BOT
    )

    scopes.should eq("bot applications.commands")
  end

  it "returns default bot scopes" do
    Medusae::Client::DiscordOAuthScopes.default_bot_scopes.should eq(["bot", "applications.commands"])
  end

  it "rejects empty scopes" do
    expect_raises(ArgumentError) { Medusae::Client::DiscordOAuthScopes.join }
    expect_raises(ArgumentError) { Medusae::Client::DiscordOAuthScopes.join(" ") }
  end
end
