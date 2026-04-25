module Medusae
  module Client
    class DiscordMessage
      EPHEMERAL_FLAG = 1 << 6

      getter content : String?
      getter embeds : Array(DiscordEmbed)
      getter components : Array(DiscordActionRow)
      getter ephemeral : Bool
      getter allowed_mentions : Payload
      getter message_reference : Payload

      def initialize(
        @content : String?,
        embeds : Enumerable(DiscordEmbed?) = [] of DiscordEmbed?,
        components : Enumerable(DiscordActionRow?) = [] of DiscordActionRow?,
        @ephemeral : Bool = false,
        allowed_mentions : Payload? = nil,
        message_reference : Payload? = nil
      )
        @embeds = embeds.compact
        @components = components.compact
        @allowed_mentions = sanitize_optional_payload(allowed_mentions)
        @message_reference = sanitize_optional_payload(message_reference)
      end

      def self.of_content(content : String?) : self
        new(content)
      end

      def self.of_embeds(content : String?, embeds : Enumerable(DiscordEmbed?)) : self
        new(content, embeds)
      end

      def self.of_components(content : String?, components : Enumerable(DiscordActionRow?)) : self
        new(content, [] of DiscordEmbed?, components)
      end

      def with_components(component_rows : Enumerable(DiscordActionRow?)) : self
        self.class.new(content, embeds, component_rows, ephemeral, allowed_mentions, message_reference)
      end

      def as_ephemeral : self
        return self if ephemeral
        self.class.new(content, embeds, components, true, allowed_mentions, message_reference)
      end

      def with_allowed_mentions(mentions : Payload?) : self
        self.class.new(content, embeds, components, ephemeral, mentions, message_reference)
      end

      def with_message_reference(reference : Payload?) : self
        self.class.new(content, embeds, components, ephemeral, allowed_mentions, reference)
      end

      def to_payload : Payload
        payload = {} of String => JSON::Any

        payload["content"] = PayloadSupport.any(content) if PayloadSupport.text?(content)

        unless embeds.empty?
          embed_payloads = embeds.map(&.to_payload).reject(&.empty?)
          payload["embeds"] = PayloadSupport.any(embed_payloads) unless embed_payloads.empty?
        end

        payload["components"] = PayloadSupport.any(components.map(&.to_payload)) unless components.empty?
        payload["flags"] = PayloadSupport.any(EPHEMERAL_FLAG) if ephemeral
        payload["allowed_mentions"] = PayloadSupport.any(allowed_mentions) unless allowed_mentions.empty?
        payload["message_reference"] = PayloadSupport.any(message_reference) unless message_reference.empty?

        payload
      end

      private def sanitize_optional_payload(value : Payload?) : Payload
        return {} of String => JSON::Any if value.nil? || value.empty?
        value.dup
      end
    end
  end
end
