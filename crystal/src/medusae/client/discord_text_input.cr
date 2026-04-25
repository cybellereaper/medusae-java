module Medusae
  module Client
    class DiscordTextInput
      include DiscordComponent

      SHORT = 1
      PARAGRAPH = 2

      getter custom_id : String
      getter style : Int32
      getter label : String
      getter min_length : Int32?
      getter max_length : Int32?
      getter required : Bool
      getter value : String?
      getter placeholder : String?

      def initialize(
        custom_id : String?,
        @style : Int32,
        @label : String,
        @min_length : Int32? = nil,
        @max_length : Int32? = nil,
        @required : Bool = true,
        @value : String? = nil,
        @placeholder : String? = nil
      )
        @custom_id = DiscordSelectMenuSupport.require_custom_id(custom_id)
        raise ArgumentError.new("label must not be blank") if @label.strip.empty?
        raise ArgumentError.new("Unsupported text input style: #{@style}") unless (SHORT..PARAGRAPH).includes?(@style)
        raise ArgumentError.new("minLength must be >= 0") if (min = @min_length) && min < 0
        raise ArgumentError.new("maxLength must be >= 1") if (max = @max_length) && max < 1

        invalid_range = (min = @min_length) && (max = @max_length) && min > max
        raise ArgumentError.new("minLength cannot be greater than maxLength") if invalid_range
      end

      def self.short_input(custom_id : String, label : String) : self
        new(custom_id, SHORT, label)
      end

      def self.paragraph(custom_id : String, label : String) : self
        new(custom_id, PARAGRAPH, label)
      end

      def with_length_range(min_length : Int32?, max_length : Int32?) : self
        self.class.new(custom_id, style, label, min_length, max_length, required, value, placeholder)
      end

      def optional : self
        return self unless required
        self.class.new(custom_id, style, label, min_length, max_length, false, value, placeholder)
      end

      def with_value(value : String?) : self
        self.class.new(custom_id, style, label, min_length, max_length, required, value, placeholder)
      end

      def with_placeholder(placeholder : String?) : self
        self.class.new(custom_id, style, label, min_length, max_length, required, value, placeholder)
      end

      def to_payload : Payload
        payload = {
          "type"      => PayloadSupport.any(4),
          "custom_id" => PayloadSupport.any(custom_id),
          "style"     => PayloadSupport.any(style),
          "label"     => PayloadSupport.any(label),
          "required"  => PayloadSupport.any(required),
        }

        payload["min_length"] = PayloadSupport.any(min_length) unless min_length.nil?
        payload["max_length"] = PayloadSupport.any(max_length) unless max_length.nil?
        payload["value"] = PayloadSupport.any(value) unless value.nil?
        payload["placeholder"] = PayloadSupport.any(placeholder) if PayloadSupport.text?(placeholder)
        payload
      end
    end
  end
end
