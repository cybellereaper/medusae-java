module Medusae
  module Support
    module JsonPayload
      extend self

      def any(value) : JSON::Any
        JSON.parse(value.to_json)
      end

      def hash : Hash(String, JSON::Any)
        {} of String => JSON::Any
      end
    end
  end
end
