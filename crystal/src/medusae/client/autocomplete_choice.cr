module Medusae
  module Client
    class AutocompleteChoice
      getter name : String
      getter value : String

      def initialize(@name : String, @value : String)
        raise ArgumentError.new("name must not be blank") if @name.strip.empty?
        raise ArgumentError.new("value must not be blank") if @value.strip.empty?
      end

      def to_payload : Hash(String, JSON::Any)
        {
          "name"  => Medusae::Support::JsonPayload.any(name),
          "value" => Medusae::Support::JsonPayload.any(value),
        }
      end
    end
  end
end
