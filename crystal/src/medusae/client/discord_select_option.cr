module Medusae
  module Client
    class DiscordSelectOption
      getter label : String
      getter value : String
      getter description : String?
      getter is_default : Bool

      def initialize(@label : String, @value : String, @description : String? = nil, @is_default : Bool = false)
        raise ArgumentError.new("label must not be blank") if @label.strip.empty?
        raise ArgumentError.new("value must not be blank") if @value.strip.empty?
      end

      def self.of(label : String, value : String) : self
        new(label, value)
      end

      def with_description(description : String?) : self
        self.class.new(label, value, description, is_default)
      end

      def as_default : self
        return self if is_default
        self.class.new(label, value, description, true)
      end

      def to_payload : Payload
        payload = {
          "label" => PayloadSupport.any(label),
          "value" => PayloadSupport.any(value),
        }
        payload["description"] = PayloadSupport.any(description) if PayloadSupport.text?(description)
        payload["default"] = PayloadSupport.any(true) if is_default
        payload
      end
    end
  end
end
