module Medusae
  module Client
    class DiscordMentionableSelectMenu < BaseEntitySelectMenu
      def self.of(custom_id : String) : self
        new(custom_id)
      end

      def with_placeholder(placeholder : String?) : self
        self.class.new(custom_id, placeholder, min_values, max_values, disabled)
      end

      def with_selection_range(min_values : Int32?, max_values : Int32?) : self
        self.class.new(custom_id, placeholder, min_values, max_values, disabled)
      end

      def disable : self
        return self if disabled
        self.class.new(custom_id, placeholder, min_values, max_values, true)
      end

      def to_payload : Payload
        base_payload(7)
      end
    end
  end
end
