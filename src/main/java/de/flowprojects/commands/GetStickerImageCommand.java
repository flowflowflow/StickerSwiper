package de.flowprojects.commands;

import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.PartialSticker;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class GetStickerImageCommand implements MessageInteractionCommand {
    @Override
    public String getName() {
        return "Get sticker image";
    }

    @Override
    public Mono<Void> handle(MessageInteractionEvent event) {
        PartialSticker sticker;
        Message message = event.getTargetMessage().block();

        try {
            sticker = message.getStickersItems().get(0);
        } catch (Exception e) {
            log.error("Couldn't retrieve sticker from message");
            return event.reply()
                    .withEphemeral(true)
                    .withContent("⚠\uFE0F Couldn't retrieve the sticker from the selected message");
        }

        if(sticker.getImageUrl().endsWith(".json")) {
            return event.reply()
                    .withEphemeral(true)
                    .withContent("⚠\uFE0F That is not a valid sticker");
        }



        return event.reply("Yippie!!!!");
    }

}
