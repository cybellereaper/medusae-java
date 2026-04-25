require "spec"
require "../src/medusae"

describe "Discord components" do
  it "message payload includes buttons and select menus" do
    button_row = Medusae::Client::DiscordActionRow.of([
      Medusae::Client::DiscordButton.primary("confirm", "Confirm"),
      Medusae::Client::DiscordButton.link("https://discord.com", "Open Docs"),
    ])

    select_row = Medusae::Client::DiscordActionRow.of([
      Medusae::Client::DiscordStringSelectMenu.of("theme", [
        Medusae::Client::DiscordSelectOption.of("Light", "light"),
        Medusae::Client::DiscordSelectOption.of("Dark", "dark").as_default,
      ]).with_placeholder("Select theme").with_selection_range(1, 1),
    ])

    payload = Medusae::Client::DiscordMessage.of_content("Choose one")
      .with_components([button_row, select_row])
      .to_payload

    payload.has_key?("components").should be_true
    payload["components"].as_a.size.should eq(2)
  end

  it "button validation rejects invalid link button" do
    expect_raises(ArgumentError) do
      Medusae::Client::DiscordButton.new(Medusae::Client::DiscordButton::LINK, "Docs", "id", nil)
    end
  end

  it "select menu validation rejects empty options" do
    expect_raises(ArgumentError) do
      Medusae::Client::DiscordStringSelectMenu.of("theme", [] of Medusae::Client::DiscordSelectOption)
    end
  end

  it "entity select menus serialize expected discord types" do
    select_row = Medusae::Client::DiscordActionRow.of([
      Medusae::Client::DiscordUserSelectMenu.of("assignee").with_placeholder("Choose user").with_selection_range(1, 1),
      Medusae::Client::DiscordRoleSelectMenu.of("roles").with_selection_range(0, 3),
      Medusae::Client::DiscordMentionableSelectMenu.of("mentions").disable,
      Medusae::Client::DiscordChannelSelectMenu.of("channels").with_channel_types([0, 2]).with_selection_range(1, 2),
    ])

    payload = Medusae::Client::DiscordMessage.of_content("Assign")
      .with_components([select_row])
      .to_payload

    rows = payload["components"].as_a
    components = rows[0]["components"].as_a

    components[0]["type"].as_i.should eq(5)
    components[1]["type"].as_i.should eq(6)
    components[2]["type"].as_i.should eq(7)
    components[3]["type"].as_i.should eq(8)
    components[3]["channel_types"].as_a.map(&.as_i).should eq([0, 2])
  end

  it "modal payload includes text inputs" do
    modal = Medusae::Client::DiscordModal.of(
      "feedback_modal",
      "Feedback",
      [
        Medusae::Client::DiscordActionRow.of([
          Medusae::Client::DiscordTextInput.short_input("summary", "Summary")
            .with_length_range(1, 100)
            .with_placeholder("Share quick feedback"),
        ]),
      ]
    )

    payload = modal.to_payload
    payload["custom_id"].as_s.should eq("feedback_modal")
    payload.has_key?("components").should be_true
  end

  it "modal validation rejects non-text-input components" do
    expect_raises(ArgumentError) do
      Medusae::Client::DiscordModal.of("id", "Title", [
        Medusae::Client::DiscordActionRow.of([
          Medusae::Client::DiscordButton.primary("a", "b"),
        ]),
      ])
    end
  end
end
