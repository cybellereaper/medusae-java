module Medusae
  module Client
    class DiscordChannelSelectMenu < BaseEntitySelectMenu
      getter channel_types : Array(Int32)?

      def initialize(custom_id : String?, placeholder : String? = nil, min_values : Int32? = nil, max_values : Int32? = nil, disabled : Bool = false, channel_types : Enumerable(Int32?)? = nil)
        sanitized = channel_types.try(&.compact)
        @channel_types = sanitized.nil? || sanitized.empty? ? nil : sanitized
        super(custom_id, placeholder, min_values, max_values, disabled)
      end

      def self.of(custom_id : String) : self
        new(custom_id)
      end

      def with_placeholder(placeholder : String?) : self
        self.class.new(custom_id, placeholder, min_values, max_values, disabled, channel_types)
      end

      def with_selection_range(min_values : Int32?, max_values : Int32?) : self
        self.class.new(custom_id, placeholder, min_values, max_values, disabled, channel_types)
      end

      def with_channel_types(channel_types : Enumerable(Int32?)) : self
        self.class.new(custom_id, placeholder, min_values, max_values, disabled, channel_types)
      end

      def disable : self
        return self if disabled
        self.class.new(custom_id, placeholder, min_values, max_values, true, channel_types)
      end

      def to_payload : Payload
        payload = base_payload(8)
        payload["channel_types"] = PayloadSupport.any(channel_types) unless channel_types.nil?
        payload
      end
    end
  end
end
