module Medusae
  module Client
    record DiscordEmbed,
      title : String?,
      description : String?,
      color : Int32?,
      url : String? = nil,
      image_url : String? = nil,
      thumbnail_url : String? = nil do

      def with_image(image_url : String?) : DiscordEmbed
        DiscordEmbed.new(title, description, color, url, image_url, thumbnail_url)
      end

      def with_thumbnail(thumbnail_url : String?) : DiscordEmbed
        DiscordEmbed.new(title, description, color, url, image_url, thumbnail_url)
      end

      def with_url(url : String?) : DiscordEmbed
        DiscordEmbed.new(title, description, color, url, image_url, thumbnail_url)
      end

      def to_payload : Payload
        payload = {} of String => JSON::Any

        put_if_text(payload, "title", title)
        put_if_text(payload, "description", description)
        payload["color"] = PayloadSupport.any(color) unless color.nil?
        put_if_text(payload, "url", url)

        if PayloadSupport.text?(image_url)
          payload["image"] = PayloadSupport.any({"url" => image_url})
        end

        if PayloadSupport.text?(thumbnail_url)
          payload["thumbnail"] = PayloadSupport.any({"url" => thumbnail_url})
        end

        payload
      end

      private def put_if_text(payload : Payload, key : String, value : String?) : Nil
        payload[key] = PayloadSupport.any(value) if PayloadSupport.text?(value)
      end
    end
  end
end
