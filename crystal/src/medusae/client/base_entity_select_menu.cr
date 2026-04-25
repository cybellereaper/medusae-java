module Medusae
  module Client
    abstract class BaseEntitySelectMenu
      include DiscordComponent

      getter custom_id : String
      getter placeholder : String?
      getter min_values : Int32?
      getter max_values : Int32?
      getter disabled : Bool

      def initialize(custom_id : String?, @placeholder : String? = nil, @min_values : Int32? = nil, @max_values : Int32? = nil, @disabled : Bool = false)
        @custom_id = DiscordSelectMenuSupport.require_custom_id(custom_id)
        DiscordSelectMenuSupport.validate_selection_range(@min_values, @max_values)
      end

      protected def base_payload(type : Int32) : Payload
        payload = {"type" => PayloadSupport.any(type)}
        DiscordSelectMenuSupport.put_shared_payload(payload, custom_id, placeholder, min_values, max_values, disabled)
        payload
      end
    end
  end
end
