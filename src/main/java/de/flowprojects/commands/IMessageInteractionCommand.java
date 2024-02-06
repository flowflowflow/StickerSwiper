package de.flowprojects.commands;

import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import reactor.core.publisher.Mono;

public interface IMessageInteractionCommand {
    public String getName();

    public Mono<Void> handle(MessageInteractionEvent event);
}
