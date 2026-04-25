module Medusae
  module Client
    class DiscordModal
      getter custom_id : String
      getter title : String
      getter components : Array(DiscordActionRow)

      def initialize(custom_id : String?, title : String?, components : Enumerable(DiscordActionRow?))
        raise ArgumentError.new("customId must not be blank") unless PayloadSupport.text?(custom_id)
        raise ArgumentError.new("title must not be blank") unless PayloadSupport.text?(title)

        filtered = components.compact
        raise ArgumentError.new("components must not be empty") if filtered.empty?

        has_invalid_component = filtered.any? do |row|
          row.components.any? { |component| !component.is_a?(DiscordTextInput) }
        end
        raise ArgumentError.new("modal components can only contain text inputs") if has_invalid_component

        @custom_id = custom_id.not_nil!
        @title = title.not_nil!
        @components = filtered
      end

      def self.of(custom_id : String, title : String, components : Enumerable(DiscordActionRow?)) : self
        new(custom_id, title, components)
      end

      def to_payload : Payload
        {
          "custom_id"  => PayloadSupport.any(custom_id),
          "title"      => PayloadSupport.any(title),
          "components" => PayloadSupport.any(components.map(&.to_payload)),
        }
      end
    end
  end
end
