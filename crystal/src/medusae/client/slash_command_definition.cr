module Medusae
  module Client
    class SlashCommandDefinition
      CHAT_INPUT = 1
      USER = 2
      MESSAGE = 3

      getter type : Int32
      getter name : String
      getter description : String?
      getter options : Array(SlashCommandOptionDefinition)
      getter default_member_permissions : String?
      getter dm_permission : Bool?
      getter nsfw : Bool?
      getter name_localizations : Hash(String, String)
      getter description_localizations : Hash(String, String)
      getter contexts : Array(Int32)

      def initialize(
        @type : Int32,
        @name : String,
        @description : String?,
        options : Enumerable(SlashCommandOptionDefinition)? = nil,
        @default_member_permissions : String? = nil,
        @dm_permission : Bool? = nil,
        @nsfw : Bool? = nil,
        name_localizations : Hash(String, String)? = nil,
        description_localizations : Hash(String, String)? = nil,
        contexts : Enumerable(Int32)? = nil
      )
        @options = (options || [] of SlashCommandOptionDefinition).to_a
        @name_localizations = name_localizations || {} of String => String
        @description_localizations = description_localizations || {} of String => String
        @contexts = (contexts || [] of Int32).to_a

        validate!
      end

      def self.new(name : String, description : String, options : Enumerable(SlashCommandOptionDefinition)? = nil)
        new(CHAT_INPUT, name, description, options)
      end

      def self.simple(name : String, description : String) : self
        new(CHAT_INPUT, name, description)
      end

      def self.user_context_menu(name : String) : self
        new(USER, name, nil)
      end

      def self.message_context_menu(name : String) : self
        new(MESSAGE, name, nil)
      end

      def with_default_member_permissions(permissions : Int64) : self
        with_default_member_permissions(Medusae::Client::DiscordPermissions.as_string(permissions))
      end

      def with_default_member_permissions(permissions_bitset : String?) : self
        copy_with(default_member_permissions: permissions_bitset)
      end

      def with_dm_permission(is_allowed_in_dm : Bool) : self
        copy_with(dm_permission: is_allowed_in_dm)
      end

      def with_nsfw(is_nsfw : Bool) : self
        copy_with(nsfw: is_nsfw)
      end

      def with_name_localizations(localizations : Hash(String, String)?) : self
        copy_with(name_localizations: localizations || {} of String => String)
      end

      def with_description_localizations(localizations : Hash(String, String)?) : self
        copy_with(description_localizations: localizations || {} of String => String)
      end

      def with_contexts(allowed_contexts : Enumerable(Int32)?) : self
        copy_with(contexts: (allowed_contexts || [] of Int32).to_a)
      end

      def to_request_payload : Hash(String, JSON::Any)
        payload = Medusae::Support::JsonPayload.hash
        payload["type"] = Medusae::Support::JsonPayload.any(type)
        payload["name"] = Medusae::Support::JsonPayload.any(name)
        payload["name_localizations"] = Medusae::Support::JsonPayload.any(name_localizations) unless name_localizations.empty?

        if type == CHAT_INPUT
          payload["description"] = Medusae::Support::JsonPayload.any(description)
          unless description_localizations.empty?
            payload["description_localizations"] = Medusae::Support::JsonPayload.any(description_localizations)
          end
          unless options.empty?
            payload["options"] = Medusae::Support::JsonPayload.any(options.map(&.to_request_payload))
          end
        end

        if default_member_permissions && !default_member_permissions.not_nil!.strip.empty?
          payload["default_member_permissions"] = Medusae::Support::JsonPayload.any(default_member_permissions)
        end

        payload["dm_permission"] = Medusae::Support::JsonPayload.any(dm_permission) unless dm_permission.nil?
        payload["nsfw"] = Medusae::Support::JsonPayload.any(nsfw) unless nsfw.nil?
        payload["contexts"] = Medusae::Support::JsonPayload.any(contexts) unless contexts.empty?

        payload
      end

      private def validate! : Nil
        raise ArgumentError.new("Unsupported application command type: #{type}") unless (CHAT_INPUT..MESSAGE).includes?(type)
        raise ArgumentError.new("name must not be blank") if name.strip.empty?

        if type == CHAT_INPUT
          raise ArgumentError.new("description must not be blank") if description.nil? || description.not_nil!.strip.empty?
        else
          raise ArgumentError.new("Context menu commands do not support options") unless options.empty?
          unless description.nil? || description.not_nil!.strip.empty?
            raise ArgumentError.new("Context menu commands do not support description")
          end
          unless description_localizations.empty?
            raise ArgumentError.new("Context menu commands do not support description localizations")
          end
        end

        if default_member_permissions && !default_member_permissions.not_nil!.strip.empty?
          unless default_member_permissions.not_nil!.chars.all?(&.ascii_number?)
            raise ArgumentError.new("defaultMemberPermissions must be a numeric string")
          end
        end
      end

      private def copy_with(
        default_member_permissions : String? = @default_member_permissions,
        dm_permission : Bool? = @dm_permission,
        nsfw : Bool? = @nsfw,
        name_localizations : Hash(String, String) = @name_localizations,
        description_localizations : Hash(String, String) = @description_localizations,
        contexts : Array(Int32) = @contexts
      ) : self
        self.class.new(type, name, description, options, default_member_permissions, dm_permission, nsfw,
          name_localizations, description_localizations, contexts)
      end
    end
  end
end
