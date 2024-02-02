package de.flowprojects.commands;

import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import reactor.core.publisher.Mono;

public class AddStickerToServerCommand implements MessageInteractionCommand {
    @Override
    public String getName() {
        return "Add sticker to this server";
    }

    @Override
    public Mono<Void> handle(MessageInteractionEvent event) {
        return event.reply("Yippe " + getName());
    }
}
