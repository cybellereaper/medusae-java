require "spec"
require "../src/medusae"

describe Medusae::Client::AutocompleteChoice do
  it "validates blank values" do
    expect_raises(ArgumentError) { Medusae::Client::AutocompleteChoice.new("", "value") }
    expect_raises(ArgumentError) { Medusae::Client::AutocompleteChoice.new("name", "") }
  end

  it "builds payload" do
    payload = Medusae::Client::AutocompleteChoice.new("name", "value").to_payload
    payload["name"].as_s.should eq("name")
    payload["value"].as_s.should eq("value")
  end
end
