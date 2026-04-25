module Medusae
  module Client
    module DiscordSelectMenuSupport
      def self.require_custom_id(custom_id : String?) : String
        raise ArgumentError.new("customId must not be blank") unless PayloadSupport.text?(custom_id)
        custom_id.not_nil!
      end

      def self.validate_selection_range(min_values : Int32?, max_values : Int32?) : Nil
        raise ArgumentError.new("minValues must be >= 0") if (min = min_values) && min < 0
        raise ArgumentError.new("maxValues must be >= 1") if (max = max_values) && max < 1

        invalid_range = (min = min_values) && (max = max_values) && min > max
        raise ArgumentError.new("minValues cannot be greater than maxValues") if invalid_range
      end

      def self.put_shared_payload(payload : Payload, custom_id : String, placeholder : String?, min_values : Int32?, max_values : Int32?, disabled : Bool) : Nil
        payload["custom_id"] = PayloadSupport.any(custom_id)
        payload["placeholder"] = PayloadSupport.any(placeholder) if PayloadSupport.text?(placeholder)
        payload["min_values"] = PayloadSupport.any(min_values) unless min_values.nil?
        payload["max_values"] = PayloadSupport.any(max_values) unless max_values.nil?
        payload["disabled"] = PayloadSupport.any(true) if disabled
      end
    end
  end
end
