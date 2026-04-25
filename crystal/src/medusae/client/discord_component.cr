module Medusae
  module Client
    module DiscordComponent
      abstract def to_payload : Payload
    end
  end
end
