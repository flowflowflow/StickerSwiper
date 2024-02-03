package de.flowprojects.commands;

import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.PartialSticker;
import discord4j.core.object.entity.Sticker;
import discord4j.discordjson.json.PartialStickerData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class GetStickerImageCommand implements MessageInteractionCommand {
    @Override
    public String getName() {
        return "Get Sticker Image";
    }

    @Override
    public Mono<Void> handle(MessageInteractionEvent event) {
        Message message = event.getTargetMessage().block();
        PartialSticker partialSticker;
        PartialStickerData stickerData;

        int stickerFormat = 0;

        long stickerId = 0;
        String stickerURL;
        String stickerName;

        final String baseURL = "https://media.discordapp.net/stickers/";

        String imageURL = "";

        String fileExtension;


        try {
            stickerData = message.getStickersItems().get(0).getStickerData();
            stickerFormat = message.getStickersItems().get(0).getStickerData().formatType();
            stickerName = stickerData.name();
            stickerId = stickerData.id().asLong();

            switch (stickerFormat) {
                case 1,2 -> fileExtension = ".png";
                case 3 -> fileExtension = ".json";
                case 4 -> fileExtension = ".gif";
                default -> fileExtension = "UNKNOWN";
            }

            if(fileExtension.equals(".json") || fileExtension.equalsIgnoreCase("UNKNOWN")) {
                return event.reply("⚠\uFE0F Could not retrieve sticker URL.");
            }

            return event.reply(imageURL.concat(baseURL).concat(Long.toString(stickerId)).concat(fileExtension));

        } catch (Exception e) {
            log.error(this.getClass().getSimpleName() + ": " + e.getMessage());

            return event.reply("⚠️ Could not retrieve sticker. Are you sure this message is a sticker?");
        }
    }

}
