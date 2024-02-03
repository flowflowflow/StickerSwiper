package de.flowprojects.util;

import de.flowprojects.StickerSwiper;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Slf4j
public class CommandManager {

    List<String> swiperBotCommands;
    String getStickerImgCmdName;
    String addStickerCmdName;


    public CommandManager() {
        getStickerImgCmdName = "Get Sticker Image";
        addStickerCmdName = "Add Sticker to Server";

        swiperBotCommands = new ArrayList<>(Arrays.asList(getStickerImgCmdName, addStickerCmdName));
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

    public void deleteGuildCommands(long applicationId, long guildId, GatewayDiscordClient gateway) {
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

    public void deleteGlobalCommands(long applicationId, GatewayDiscordClient gateway) {
        Map<String, ApplicationCommandData> discordCommands = gateway.getRestClient()
                .getApplicationService()
                .getGlobalApplicationCommands(applicationId)
                .collectMap(ApplicationCommandData::name)
                .block();

        //discordCommands.entrySet().removeIf(entry -> !entry.getValue().equals(swiperBotCommands));

        for(Map.Entry<String, ApplicationCommandData> entry : discordCommands.entrySet()) {
            long commandId = entry.getValue().id().asLong();
            gateway.getRestClient()
                    .getApplicationService()
                    .deleteGlobalApplicationCommand(applicationId, commandId);

            log.info(StickerSwiper.class + "deleteAllGlobalCommands deleted the following commands: " + entry.getKey());
        }
    }
}
