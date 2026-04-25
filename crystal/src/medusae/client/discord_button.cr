module Medusae
  module Client
    class DiscordButton
      include DiscordComponent

      PRIMARY = 1
      SECONDARY = 2
      SUCCESS = 3
      DANGER = 4
      LINK = 5

      getter style : Int32
      getter label : String
      getter custom_id : String?
      getter url : String?
      getter emoji : String?
      getter disabled : Bool

      def initialize(@style : Int32, @label : String, @custom_id : String? = nil, @url : String? = nil, @emoji : String? = nil, @disabled : Bool = false)
        validate!
      end

      def self.primary(custom_id : String, label : String) : self
        new(PRIMARY, label, custom_id)
      end

      def self.link(url : String, label : String) : self
        new(LINK, label, nil, url)
      end

      def with_emoji(emoji_name : String?) : self
        self.class.new(style, label, custom_id, url, emoji_name, disabled)
      end

      def disable : self
        return self if disabled
        self.class.new(style, label, custom_id, url, emoji, true)
      end

      def to_payload : Payload
        payload = {
          "type"  => PayloadSupport.any(2),
          "style" => PayloadSupport.any(style),
          "label" => PayloadSupport.any(label),
        }

        payload["custom_id"] = PayloadSupport.any(custom_id) if PayloadSupport.text?(custom_id)
        payload["url"] = PayloadSupport.any(url) if PayloadSupport.text?(url)
        payload["emoji"] = PayloadSupport.any({"name" => emoji}) if PayloadSupport.text?(emoji)
        payload["disabled"] = PayloadSupport.any(true) if disabled
        payload
      end

      private def validate! : Nil
        raise ArgumentError.new("label must not be blank") if label.strip.empty?

        unless (PRIMARY..LINK).includes?(style)
          raise ArgumentError.new("Unsupported button style: #{style}")
        end

        has_custom_id = PayloadSupport.text?(custom_id)
        has_url = PayloadSupport.text?(url)

        if style == LINK
          valid = has_url && !has_custom_id
          raise ArgumentError.new("Link buttons require url and cannot set customId") unless valid
        else
          valid = has_custom_id && !has_url
          raise ArgumentError.new("Non-link buttons require customId and cannot set url") unless valid
        end
      end
    end
  end
end
