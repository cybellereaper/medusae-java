module Medusae
  module Client
    alias Payload = Hash(String, JSON::Any)

    module PayloadSupport
      def self.text?(value : String?) : Bool
        !value.nil? && !value.not_nil!.strip.empty?
      end

      def self.any(value) : JSON::Any
        JSON.parse(value.to_json)
      end
    end
  end
end
