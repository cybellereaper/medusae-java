require "spec"
require "../src/medusae"

describe Medusae::Client::DiscordPermissions do
  it "combines flags and serializes as unsigned string" do
    value = Medusae::Client::DiscordPermissions.of(
      Medusae::Client::DiscordPermissions::SEND_MESSAGES,
      Medusae::Client::DiscordPermissions::USE_APPLICATION_COMMANDS
    )

    value.should eq((1_i64 << 11) | (1_i64 << 31))
    Medusae::Client::DiscordPermissions.as_string(value).should eq(value.to_u64.to_s)
  end

  it "rejects negative values" do
    expect_raises(ArgumentError) { Medusae::Client::DiscordPermissions.of(-1_i64) }
    expect_raises(ArgumentError) { Medusae::Client::DiscordPermissions.as_string(-1_i64) }
  end
end
