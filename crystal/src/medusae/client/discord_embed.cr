module Medusae
  module Client
    class DiscordEmbed
      getter title : String?
      getter description : String?
      getter color : Int32?
      getter url : String?
      getter image_url : String?
      getter thumbnail_url : String?

      def initialize(
        @title : String?,
        @description : String?,
        @color : Int32?,
        @url : String? = nil,
        @image_url : String? = nil,
        @thumbnail_url : String? = nil
      )
      end

      def with_image(image_url : String?) : DiscordEmbed
        DiscordEmbed.new(title, description, color, url, image_url, thumbnail_url)
      end

      def with_thumbnail(thumbnail_url : String?) : DiscordEmbed
        DiscordEmbed.new(title, description, color, url, image_url, thumbnail_url)
      end

      def with_url(url : String?) : DiscordEmbed
        DiscordEmbed.new(title, description, color, url, image_url, thumbnail_url)
      end

      def to_payload : Hash(String, JSON::Any)
        payload = Medusae::Support::JsonPayload.hash

        put_if_text(payload, "title", title)
        put_if_text(payload, "description", description)
        payload["color"] = Medusae::Support::JsonPayload.any(color) if color
        put_if_text(payload, "url", url)

        if text?(image_url)
          payload["image"] = Medusae::Support::JsonPayload.any({"url" => image_url.not_nil!})
        end

        if text?(thumbnail_url)
          payload["thumbnail"] = Medusae::Support::JsonPayload.any({"url" => thumbnail_url.not_nil!})
        end

        payload
      end

      private def put_if_text(payload : Hash(String, JSON::Any), key : String, value : String?) : Nil
        payload[key] = Medusae::Support::JsonPayload.any(value.not_nil!) if text?(value)
      end

      private def text?(value : String?) : Bool
        !value.nil? && !value.not_nil!.strip.empty?
      end
    end
  end
end
