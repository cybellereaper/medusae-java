module Medusae
  module Client
    module DiscordOAuthScopes
      BOT = "bot"
      APPLICATIONS_COMMANDS = "applications.commands"
      IDENTIFY = "identify"
      GUILDS = "guilds"
      EMAIL = "email"

      extend self

      def join : String
        raise ArgumentError.new("at least one scope is required")
      end

      def join(*scopes : String) : String
        ordered = [] of String

        scopes.each do |scope|
          value = scope.strip
          raise ArgumentError.new("scope must not be blank") if value.empty?
          ordered << value unless ordered.includes?(value)
        end

        raise ArgumentError.new("at least one scope is required") if ordered.empty?

        ordered.join(" ")
      end

      def default_bot_scopes : Array(String)
        [BOT, APPLICATIONS_COMMANDS]
      end

      def normalize(scopes : Enumerable(String)) : String
        join(*scopes.to_a)
      end
    end
  end
end
