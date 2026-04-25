module Medusae
  module Client
    class SlashCommandOptionDefinition
      SUBCOMMAND = 1
      SUBCOMMAND_GROUP = 2
      STRING = 3
      INTEGER = 4
      BOOLEAN = 5
      USER = 6
      CHANNEL = 7
      ROLE = 8
      MENTIONABLE = 9
      NUMBER = 10
      ATTACHMENT = 11

      getter type : Int32
      getter name : String
      getter description : String
      getter required : Bool
      getter autocomplete : Bool
      getter options : Array(SlashCommandOptionDefinition)

      def initialize(
        @type : Int32,
        @name : String,
        @description : String,
        @required : Bool,
        @autocomplete : Bool,
        options : Enumerable(SlashCommandOptionDefinition)? = nil
      )
        raise ArgumentError.new("Unsupported slash command option type: #{@type}") unless (SUBCOMMAND..ATTACHMENT).includes?(@type)
        raise ArgumentError.new("name must not be blank") if @name.strip.empty?
        raise ArgumentError.new("description must not be blank") if @description.strip.empty?

        @options = (options || [] of SlashCommandOptionDefinition).to_a
      end

      def self.string(name : String, description : String, required : Bool) : self
        new(STRING, name, description, required, false)
      end

      def self.autocompleted_string(name : String, description : String, required : Bool) : self
        new(STRING, name, description, required, true)
      end

      def self.subcommand(name : String, description : String, options : Enumerable(SlashCommandOptionDefinition)? = nil) : self
        new(SUBCOMMAND, name, description, false, false, options)
      end

      def self.subcommand_group(name : String, description : String, options : Enumerable(SlashCommandOptionDefinition)? = nil) : self
        new(SUBCOMMAND_GROUP, name, description, false, false, options)
      end

      def to_request_payload : Hash(String, JSON::Any)
        payload = Medusae::Support::JsonPayload.hash
        payload["type"] = Medusae::Support::JsonPayload.any(type)
        payload["name"] = Medusae::Support::JsonPayload.any(name)
        payload["description"] = Medusae::Support::JsonPayload.any(description)

        if type >= STRING
          payload["required"] = Medusae::Support::JsonPayload.any(required)
          payload["autocomplete"] = Medusae::Support::JsonPayload.any(true) if autocomplete
        end

        unless options.empty?
          payload["options"] = Medusae::Support::JsonPayload.any(options.map(&.to_request_payload))
        end

        payload
      end
    end
  end
end
