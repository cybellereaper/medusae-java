module Medusae
  module Client
    module DiscordPermissions
      CREATE_INSTANT_INVITE = 1_i64 << 0
      KICK_MEMBERS = 1_i64 << 1
      BAN_MEMBERS = 1_i64 << 2
      ADMINISTRATOR = 1_i64 << 3
      MANAGE_CHANNELS = 1_i64 << 4
      MANAGE_GUILD = 1_i64 << 5
      ADD_REACTIONS = 1_i64 << 6
      VIEW_AUDIT_LOG = 1_i64 << 7
      VIEW_CHANNEL = 1_i64 << 10
      SEND_MESSAGES = 1_i64 << 11
      MANAGE_MESSAGES = 1_i64 << 13
      EMBED_LINKS = 1_i64 << 14
      ATTACH_FILES = 1_i64 << 15
      READ_MESSAGE_HISTORY = 1_i64 << 16
      USE_EXTERNAL_EMOJIS = 1_i64 << 18
      CONNECT = 1_i64 << 20
      SPEAK = 1_i64 << 21
      MUTE_MEMBERS = 1_i64 << 22
      DEAFEN_MEMBERS = 1_i64 << 23
      MOVE_MEMBERS = 1_i64 << 24
      MANAGE_ROLES = 1_i64 << 28
      MANAGE_WEBHOOKS = 1_i64 << 29
      USE_APPLICATION_COMMANDS = 1_i64 << 31

      extend self

      def of(*permissions : Int64) : Int64
        value = 0_i64
        permissions.each do |permission|
          raise ArgumentError.new("permission values must be non-negative") if permission < 0
          value |= permission
        end
        value
      end

      def as_string(permissions : Int64) : String
        raise ArgumentError.new("permissions must be non-negative") if permissions < 0
        permissions.to_u64.to_s
      end
    end
  end
end
