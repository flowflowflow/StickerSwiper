package de.flowprojects;

import de.flowprojects.listeners.MessageInteractionEventListener;
import de.flowprojects.util.Constants;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Status;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

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



        //Create GDClient and connect it to Discord
        final DiscordClient client = DiscordClient.create(discordApiToken);
        GatewayDiscordClient gateway = client.gateway()
                .setEnabledIntents(IntentSet.nonPrivileged()
                        .and(getIntents()))
                .setInitialPresence(shard -> ClientPresence.of(Status.ONLINE, ClientActivity.playing("Swiping Stickers >:3c")))
                .login().block();

        //Build Swipe command request to be used for command registration
        ApplicationCommandRequest stickerImageCommandRequest = getGetStickerImageCommandRequest();
        ApplicationCommandRequest addStickerToServerCommandRequest = getAddStickerToServerCommandRequest();

        //GUILD command registration for swipe command
        gateway.getRestClient().getApplicationService()
                .createGuildApplicationCommand(applicationId, guildId, stickerImageCommandRequest)
                .subscribe();

        gateway.getRestClient().getApplicationService()
                .createGuildApplicationCommand(applicationId, guildId, addStickerToServerCommandRequest)
                .subscribe();


        /*
        //GLOBAL command registration for swipe command
        client.getRestClient().getApplicationService()
                .createGlobalApplicationCommand(applicationId, swipeCmdRequest)
                .subscribe();
        */


        //Update online status and activity
        /*ÜÜ
        gateway.updatePresence(
                ClientPresence.of(Status.ONLINE, ClientActivity.playing("Swiping Stickers >:3c"))
        ).subscribe();
        */


        //Handle various events
        gateway.on(MessageInteractionEvent.class, MessageInteractionEventListener::handle).subscribe();

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

        discordCommands.entrySet().removeIf(entry -> !entry.getValue().equals(swiperBotCommands));

        for(Map.Entry<String, ApplicationCommandData> entry : discordCommands.entrySet()) {
            long commandId = entry.getValue().id().asLong();
            gateway.getRestClient()
                    .getApplicationService()
                    .deleteGuildApplicationCommand(applicationId, guildId, commandId);
        }

        log.info(StickerSwiper.class + "deleteAllGuildCommands deleted the following commands: " + discordCommands);
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
        }

        log.info(StickerSwiper.class + "deleteAllGuildCommands deleted the following commands: " + discordCommands);
    }


    //TODO: add modify commands methods


    private static ApplicationCommandRequest getGetStickerImageCommandRequest() {
        final String commandName = "Get Sticker Image";

        if (!swiperBotCommands.contains(commandName)) {
            swiperBotCommands.add(commandName);
        }


        return ApplicationCommandRequest.builder()
                .name(commandName)
                .type(3)
                .build();
    }

    private static ApplicationCommandRequest getAddStickerToServerCommandRequest() {
        final String commandName = "Add Sticker to Server";

        if (!swiperBotCommands.contains(commandName)) {
            swiperBotCommands.add(commandName);
        }

        return ApplicationCommandRequest.builder()
                .name(commandName)
                .type(3)
                .build();
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
