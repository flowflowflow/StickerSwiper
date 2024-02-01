package de.flowprojects.commands;

import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class SwipeCommand implements MessageInteractionCommand {
    @Override
    public String getName() {
        return "Swipe";
    }

    @Override
    public Mono<Void> handle(MessageInteractionEvent event) {
        return event.reply("Yippie!!!!");
    }
}
