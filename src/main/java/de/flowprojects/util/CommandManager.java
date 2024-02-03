package de.flowprojects.util;

import de.flowprojects.StickerSwiper;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Slf4j
public class CommandManager {

    final long appId = Long.parseLong(Constants.APP_ID.value);
    final long guildId = Long.parseLong(Constants.GUILD_ID.value);

    final List<String> swiperBotCommands;
    String getStickerImgCmdName;
    String addStickerCmdName;


    public CommandManager() {
        getStickerImgCmdName = "Get Sticker Image";
        addStickerCmdName = "Add Sticker to Server";

        swiperBotCommands = new ArrayList<>(Arrays.asList(getStickerImgCmdName, addStickerCmdName));
    }

    public void registerGuildCommands(GatewayDiscordClient gateway, List<ApplicationCommandRequest> commandRequests) {
        for(ApplicationCommandRequest request : commandRequests) {
            gateway.getRestClient()
                    .getApplicationService()
                    .createGuildApplicationCommand(appId, guildId, request)
                    .subscribe();

            log.info(this.getClass().getSimpleName() + "Registered command: " + request.name());
        }
    }

    public void registerGlobalCommands(GatewayDiscordClient gateway, List<ApplicationCommandRequest> commandRequests) {
        for(ApplicationCommandRequest request : commandRequests) {
            gateway.getRestClient()
                    .getApplicationService()
                    .createGlobalApplicationCommand(appId, request)
                    .subscribe();

            log.info(this.getClass().getSimpleName() + "Registered command: " + request.name());
        }
    }

    public ApplicationCommandRequest getAddStickerToServerCommandRequest() {
        return ApplicationCommandRequest.builder()
                .name(addStickerCmdName)
                .type(3)
                .build();
    }

    public ApplicationCommandRequest getGetStickerImageCommandRequest() {
        return ApplicationCommandRequest.builder()
                .name(getStickerImgCmdName)
                .type(3)
                .build();
    }

    public void deleteGuildCommands(GatewayDiscordClient gateway) {
        Map<String, ApplicationCommandData> discordCommands = gateway.getRestClient()
                .getApplicationService()
                .getGuildApplicationCommands(appId, guildId)
                .collectMap(ApplicationCommandData::name)
                .block();

        //Clean out map
        //discordCommands.entrySet().removeIf(entry -> !entry.getValue().equals(swiperBotCommands));

        for(Map.Entry<String, ApplicationCommandData> entry : discordCommands.entrySet()) {
            long commandId = entry.getValue().id().asLong();
            gateway.getRestClient()
                    .getApplicationService()
                    .deleteGuildApplicationCommand(appId, guildId, commandId);

            log.info(StickerSwiper.class.getSimpleName() + ": deleteAllGuildCommands deleted the following commands: " + entry.getKey());
        }
    }

    public void deleteGlobalCommands(GatewayDiscordClient gateway) {
        Map<String, ApplicationCommandData> discordCommands = gateway.getRestClient()
                .getApplicationService()
                .getGlobalApplicationCommands(appId)
                .collectMap(ApplicationCommandData::name)
                .block();

        //Clean out map
        //discordCommands.entrySet().removeIf(entry -> !entry.getValue().equals(swiperBotCommands));

        for(Map.Entry<String, ApplicationCommandData> entry : discordCommands.entrySet()) {
            long commandId = entry.getValue().id().asLong();
            gateway.getRestClient()
                    .getApplicationService()
                    .deleteGlobalApplicationCommand(appId, commandId);

            log.info(StickerSwiper.class + ": deleteAllGlobalCommands deleted the following commands: " + entry.getKey());
        }
    }
}
