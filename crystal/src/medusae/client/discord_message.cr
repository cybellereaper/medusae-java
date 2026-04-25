module Medusae
  module Client
    class DiscordMessage
      EPHEMERAL_FLAG = 1 << 6

      getter content : String?
      getter embeds : Array(DiscordEmbed)
      getter components : Array(Hash(String, JSON::Any))
      getter ephemeral : Bool
      getter allowed_mentions : Hash(String, JSON::Any)
      getter message_reference : Hash(String, JSON::Any)

      def initialize(
        @content : String?,
        embeds : Enumerable(DiscordEmbed?)? = nil,
        components : Enumerable(Hash(String, JSON::Any)?)? = nil,
        @ephemeral : Bool = false,
        allowed_mentions : Hash(String, JSON::Any)? = nil,
        message_reference : Hash(String, JSON::Any)? = nil
      )
        @embeds = (embeds || [] of DiscordEmbed?).compact.to_a
        @components = (components || [] of Hash(String, JSON::Any)?).compact.to_a
        @allowed_mentions = allowed_mentions || Medusae::Support::JsonPayload.hash
        @message_reference = message_reference || Medusae::Support::JsonPayload.hash
      end

      def self.of_content(content : String?) : DiscordMessage
        new(content)
      end

      def self.of_embeds(content : String?, embeds : Enumerable(DiscordEmbed?)?) : DiscordMessage
        new(content, embeds: embeds)
      end

      def self.of_components(content : String?, components : Enumerable(Hash(String, JSON::Any)?)?) : DiscordMessage
        new(content, components: components)
      end

      def with_components(component_rows : Enumerable(Hash(String, JSON::Any)?)?) : DiscordMessage
        DiscordMessage.new(content, embeds: embeds, components: component_rows, ephemeral: ephemeral,
          allowed_mentions: allowed_mentions, message_reference: message_reference)
      end

      def as_ephemeral : DiscordMessage
        return self if ephemeral

        DiscordMessage.new(content, embeds: embeds, components: components, ephemeral: true,
          allowed_mentions: allowed_mentions, message_reference: message_reference)
      end

      def with_allowed_mentions(mentions : Hash(String, JSON::Any)?) : DiscordMessage
        DiscordMessage.new(content, embeds: embeds, components: components, ephemeral: ephemeral,
          allowed_mentions: mentions, message_reference: message_reference)
      end

      def with_message_reference(reference : Hash(String, JSON::Any)?) : DiscordMessage
        DiscordMessage.new(content, embeds: embeds, components: components, ephemeral: ephemeral,
          allowed_mentions: allowed_mentions, message_reference: reference)
      end

      def to_payload : Hash(String, JSON::Any)
        payload = Medusae::Support::JsonPayload.hash

        if content && !content.not_nil!.strip.empty?
          payload["content"] = Medusae::Support::JsonPayload.any(content)
        end

        embed_payloads = embeds.map(&.to_payload).reject(&.empty?)
        payload["embeds"] = Medusae::Support::JsonPayload.any(embed_payloads) unless embed_payloads.empty?

        payload["components"] = Medusae::Support::JsonPayload.any(components) unless components.empty?
        payload["flags"] = Medusae::Support::JsonPayload.any(EPHEMERAL_FLAG) if ephemeral
        payload["allowed_mentions"] = Medusae::Support::JsonPayload.any(allowed_mentions) unless allowed_mentions.empty?
        payload["message_reference"] = Medusae::Support::JsonPayload.any(message_reference) unless message_reference.empty?

        payload
      end
    end
  end
end
