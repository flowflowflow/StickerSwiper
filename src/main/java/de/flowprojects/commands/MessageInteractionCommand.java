package de.flowprojects.commands;

import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface MessageInteractionCommand {
    public String getName();

    public Mono<Void> handle(MessageInteractionEvent event);
}
