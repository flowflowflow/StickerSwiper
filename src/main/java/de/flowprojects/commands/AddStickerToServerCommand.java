package de.flowprojects.commands;

import de.flowprojects.util.StickerUtil;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.GuildStickerCreateSpec;
import discord4j.discordjson.json.PartialStickerData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class AddStickerToServerCommand implements IMessageInteractionCommand {
    @Override
    public String getName() {
        return "Add sticker to server";
    }

    @Override
    public Mono<Void> handle(MessageInteractionEvent event) {
        try {
            Message message = event.getTargetMessage().block();
            Guild guild = message.getGuild().block();

            PartialStickerData stickerData = message.getStickersItems().get(0).getStickerData();
            int stickerFormat = stickerData.formatType();
            long stickerId = stickerData.id().asLong();
            String stickerURL = StickerUtil.getStickerURL(stickerFormat, stickerId);
            String stickerName = stickerData.name();

            if(!stickerURL.endsWith(".gif") || !stickerURL.endsWith(".png")) {

                // Does not work :(
                guild.createSticker(GuildStickerCreateSpec.builder()
                        .file(stickerURL)
                        .name(stickerName)
                        .description("Added automatically by StickerSwiper bot")
                        .tags("StickerSwiper")
                        .build())
                        .block();

                return event.reply("Added sticker " + stickerName + " to this guild!");
            }

            return event.reply("⚠\uFE0F Wrong format!");
        } catch (Exception e) {
            log.error(e.getClass().getSimpleName() +  ": " + e.getMessage());
            return event.reply("⚠\uFE0F There was an error while trying to add the sticker to this guild. Are you sure the selected message is a sticker?");
        }
    }
}
