package de.flowprojects;

import de.flowprojects.listeners.MessageInteractionEventListener;
import de.flowprojects.util.CommandManager;
import de.flowprojects.util.Constants;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Status;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public class StickerSwiper
{

    static List<String> swiperBotCommands = new ArrayList<>();

    public static void main( String[] args )
    {
        final String discordApiToken =  Constants.DISCORD_API_TOKEN.value;
        final long applicationId = Long.parseLong(Constants.APP_ID.value);
        final long guildId = Long.parseLong(Constants.GUILD_ID.value);

        CommandManager cmdManager = new CommandManager();

        //Build Swipe command request to be used for command registration
        ApplicationCommandRequest stickerImageCommandRequest = cmdManager.getGetStickerImageCommandRequest();
        ApplicationCommandRequest addStickerToServerCommandRequest = cmdManager.getAddStickerToServerCommandRequest();

        //Create GDClient and connect it to Discord
        final DiscordClient client = DiscordClient.create(discordApiToken);
        GatewayDiscordClient gateway = client.gateway()
                .setEnabledIntents(IntentSet.nonPrivileged()
                        .and(getIntents()))
                .setInitialPresence(shard -> ClientPresence.of(Status.ONLINE, ClientActivity.playing("Swiping Stickers >:3c")))
                .login().block();

        //GUILD command registration for swipe command

        gateway.getRestClient().getApplicationService()
                .createGuildApplicationCommand(applicationId, guildId, stickerImageCommandRequest)
                .subscribe();

        gateway.getRestClient().getApplicationService()
                .createGuildApplicationCommand(applicationId, guildId, addStickerToServerCommandRequest)
                .subscribe();




        //Handle various events
        gateway.on(MessageInteractionEvent.class, MessageInteractionEventListener::handle).subscribe();

        //Testing
        gateway.on(MessageCreateEvent.class, event -> {
            if(event.getMessage().getContent().equals("deleteguildcommands")) {
                deleteGuildCommands(applicationId, guildId, gateway);
            }
            if(event.getMessage().getContent().equals("deleteglobalcommands")) {
                deleteGlobalCommands(applicationId, gateway);
            }

            return Mono.empty();
        }).subscribe();

        gateway.on(ReadyEvent.class, event -> {
            log.info("Logged in with {}", gateway.getSelf().block().getUsername());
            return Mono.empty();
        }).then(gateway.onDisconnect()).block(); //needed at the end of the last client.on

    }

    //Probably best solution...
    private static void deleteGuildCommands(long applicationId, long guildId, GatewayDiscordClient gateway) {
        Map<String, ApplicationCommandData> discordCommands = gateway.getRestClient()
                .getApplicationService()
                .getGuildApplicationCommands(applicationId, guildId)
                .collectMap(ApplicationCommandData::name)
                .block();

        //Clean out map
        //discordCommands.entrySet().removeIf(entry -> !entry.getValue().equals(swiperBotCommands));

        for(Map.Entry<String, ApplicationCommandData> entry : discordCommands.entrySet()) {
            long commandId = entry.getValue().id().asLong();
            gateway.getRestClient()
                    .getApplicationService()
                    .deleteGuildApplicationCommand(applicationId, guildId, commandId);

            log.info(StickerSwiper.class.getSimpleName() + ": deleteAllGuildCommands deleted the following commands: " + entry.getKey());
        }
    }

    private static void deleteGlobalCommands(long applicationId, GatewayDiscordClient gateway) {
        Map<String, ApplicationCommandData> discordCommands = gateway.getRestClient()
                .getApplicationService()
                .getGlobalApplicationCommands(applicationId)
                .collectMap(ApplicationCommandData::name)
                .block();

        discordCommands.entrySet().removeIf(entry -> !entry.getValue().equals(swiperBotCommands));

        for(Map.Entry<String, ApplicationCommandData> entry : discordCommands.entrySet()) {
            long commandId = entry.getValue().id().asLong();
            gateway.getRestClient()
                    .getApplicationService()
                    .deleteGlobalApplicationCommand(applicationId, commandId);

            log.info(StickerSwiper.class + "deleteAllGlobalCommands deleted the following commands: " + entry.getKey());
        }
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
