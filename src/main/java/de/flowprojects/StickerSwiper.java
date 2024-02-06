package de.flowprojects;

import de.flowprojects.listeners.MessageInteractionEventListener;
import de.flowprojects.util.CommandManager;
import de.flowprojects.util.Constants;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Status;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class StickerSwiper
{

    public static void main( String[] args )
    {
        final String discordApiToken =  Constants.DISCORD_API_TOKEN.value;
        final long applicationId = Long.parseLong(Constants.APP_ID.value);
        final long guildId = Long.parseLong(Constants.GUILD_ID.value);

        CommandManager cmdManager = new CommandManager();

        //Build command requests and put them in the list for registration after the bot starts
        List<ApplicationCommandRequest> commandRequests = new ArrayList<>();
        commandRequests.add(cmdManager.getGetStickerImageCommandRequest());
        commandRequests.add(cmdManager.getAddStickerToServerCommandRequest());

        // Create GDClient and connect it to Discord
        final DiscordClient client = DiscordClient.create(discordApiToken);
        GatewayDiscordClient gateway = client.gateway()
                .setEnabledIntents(IntentSet.nonPrivileged()
                        .and(getIntents()))
                .setInitialPresence(shard -> ClientPresence.of(Status.ONLINE, ClientActivity.playing("Swiping Stickers >:3c")))
                .login().block();

        // Delete any existing commands
        cmdManager.deleteGuildCommands(gateway);

        //Register guild commands from this bot
        //cmdManager.registerGuildCommands(gateway, commandRequests);
        cmdManager.registerGlobalCommands(gateway, commandRequests);

        // Event handling
        gateway.on(MessageInteractionEvent.class, MessageInteractionEventListener::handle).subscribe();

        gateway.on(ReadyEvent.class, event -> {
            log.info("Logged in with {}", gateway.getSelf().block().getUsername());
            return Mono.empty();
        }).then(gateway.onDisconnect()).block(); //needed at the end of the last client.on

    }

    private static IntentSet getIntents() {
        return IntentSet.of(
                Intent.GUILD_MESSAGES,
                Intent.GUILD_MESSAGE_REACTIONS,
                Intent.GUILD_MESSAGE_TYPING,
                Intent.MESSAGE_CONTENT,
                Intent.GUILD_EMOJIS_AND_STICKERS
        );
    }
}
