module Medusae
  module Client
    class DiscordActionRow
      getter components : Array(DiscordComponent)

      def initialize(components : Enumerable(DiscordComponent?))
        filtered = [] of DiscordComponent
        components.each do |component|
          filtered << component if component
        end
        raise ArgumentError.new("components must not be empty") if filtered.empty?
        @components = filtered
      end

      def self.of(components : Enumerable(DiscordComponent?)) : self
        new(components)
      end

      def to_payload : Payload
        {
          "type"       => PayloadSupport.any(1),
          "components" => PayloadSupport.any(@components.map(&.to_payload)),
        }
      end
    end
  end
end
