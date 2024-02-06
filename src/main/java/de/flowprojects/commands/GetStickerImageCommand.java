package de.flowprojects.commands;

import de.flowprojects.util.StickerUtil;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.PartialSticker;
import discord4j.discordjson.json.PartialStickerData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class GetStickerImageCommand implements IMessageInteractionCommand {
    @Override
    public String getName() {
        return "Get Sticker Image";
    }

    @Override
    public Mono<Void> handle(MessageInteractionEvent event) {

        Message message = event.getTargetMessage().block();
        PartialStickerData stickerData;
        int stickerFormat = 0;
        long stickerId = 0;

        try {
            stickerData = message.getStickersItems().get(0).getStickerData();
            stickerFormat = stickerData.formatType();
            stickerId = stickerData.id().asLong();

            if(StickerUtil.getStickerExtension(stickerFormat).equals(".json")) {
                return event.reply("⚠\uFE0F Could not retrieve sticker URL.");
            }

            return event.reply(StickerUtil.getStickerURL(stickerFormat, stickerId));

            //return event.reply(baseURL.concat(Long.toString(stickerId)).concat(fileExtension));

        } catch (Exception e) {
            log.error(this.getClass().getSimpleName() + ": " + e.getMessage());

            return event.reply("⚠️ Could not retrieve sticker. Are you sure this message is a sticker?");
        }
    }

}
