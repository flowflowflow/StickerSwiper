package de.flowprojects;

import de.flowprojects.util.Constants;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.beans.BeanProperty;


@Slf4j
public class StickerSwiper
{
    public static void main( String[] args )
    {
        final String discordApiToken =  Constants.DISCORD_API_TOKEN.value;
        ApplicationCommandRequest swipeCmdRequest = getSwipeCommandRequest();

        GatewayDiscordClient client = DiscordClientBuilder.create(discordApiToken).build().login().block();

        client.on(ReadyEvent.class, event -> {
            log.info("Logged in with {}", client.getSelf().block().getUsername());
            return Mono.empty();
        }).then(client.onDisconnect()).block(); //needed at the end of the last client.on

    }


    private static ApplicationCommandRequest getSwipeCommandRequest() {
        return ApplicationCommandRequest.builder()
                .name("swipe")
                .build();
    }

    private static IntentSet getIntents() {
        return IntentSet.of(
                Intent.GUILD_MESSAGES,
                Intent.GUILD_MESSAGE_REACTIONS,
                Intent.GUILD_MESSAGE_TYPING,
                Intent.MESSAGE_CONTENT
        );
    }
}
