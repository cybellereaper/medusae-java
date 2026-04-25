module Medusae
  module Client
    class DiscordStringSelectMenu < BaseEntitySelectMenu
      getter options : Array(DiscordSelectOption)

      def initialize(custom_id : String?, options : Enumerable(DiscordSelectOption?), placeholder : String? = nil, min_values : Int32? = nil, max_values : Int32? = nil, disabled : Bool = false)
        @options = options.compact
        raise ArgumentError.new("options must not be empty") if @options.empty?
        super(custom_id, placeholder, min_values, max_values, disabled)
      end

      def self.of(custom_id : String, options : Enumerable(DiscordSelectOption?)) : self
        new(custom_id, options)
      end

      def with_placeholder(placeholder : String?) : self
        self.class.new(custom_id, options, placeholder, min_values, max_values, disabled)
      end

      def with_selection_range(min_values : Int32?, max_values : Int32?) : self
        self.class.new(custom_id, options, placeholder, min_values, max_values, disabled)
      end

      def disable : self
        return self if disabled
        self.class.new(custom_id, options, placeholder, min_values, max_values, true)
      end

      def to_payload : Payload
        payload = base_payload(3)
        payload["options"] = PayloadSupport.any(options.map(&.to_payload))
        payload
      end
    end
  end
end
