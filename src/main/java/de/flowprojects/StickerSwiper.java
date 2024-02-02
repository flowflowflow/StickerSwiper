package de.flowprojects;

import de.flowprojects.listeners.MessageInteractionEventListener;
import de.flowprojects.util.Constants;
import discord4j.core.DiscordClientBuilder;
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


@Slf4j
public class StickerSwiper
{
    public static void main( String[] args )
    {
        final String discordApiToken =  Constants.DISCORD_API_TOKEN.value;
        final long applicationId = Long.parseLong(Constants.APP_ID.value);
        final long guildId = Long.parseLong(Constants.GUILD_ID.value);

        //Create GDClient and connect it to Discord
        GatewayDiscordClient client = DiscordClientBuilder.create(discordApiToken).build().login().block();

        //Build Swipe command request to be used for command registration
        ApplicationCommandRequest stickerImageCommandRequest = getGetStickerImageCommandRequest();
        ApplicationCommandRequest addStickerToServerCommandRequest = getAddStickerToServerCommandRequest();

        //GUILD command registration for swipe command
        client.getRestClient().getApplicationService()
                .createGuildApplicationCommand(applicationId, guildId, stickerImageCommandRequest)
                .subscribe();

        client.getRestClient().getApplicationService()
                .createGuildApplicationCommand(applicationId, guildId, addStickerToServerCommandRequest);

        /*
        //GLOBAL command registration for swipe command
        client.getRestClient().getApplicationService()
                .createGlobalApplicationCommand(applicationId, swipeCmdRequest)
                .subscribe();
        */


        //Update online status and activity
        client.updatePresence(
                ClientPresence.of(Status.ONLINE, ClientActivity.playing("Swiping Stickers >:3c"))
        ).subscribe();


        //Handle various events
        client.on(MessageInteractionEvent.class, MessageInteractionEventListener::handle).subscribe();

        client.on(ReadyEvent.class, event -> {
            log.info("Logged in with {}", client.getSelf().block().getUsername());
            return Mono.empty();
        }).then(client.onDisconnect()).block(); //needed at the end of the last client.on

    }


    private static ApplicationCommandRequest getGetStickerImageCommandRequest() {
        return ApplicationCommandRequest.builder()
                .name("Get sticker image")
                .type(3)
                .build();
    }

    private static ApplicationCommandRequest getAddStickerToServerCommandRequest() {
        return ApplicationCommandRequest.builder()
                .name("Add sticker to this server")
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
