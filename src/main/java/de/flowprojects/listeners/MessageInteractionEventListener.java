package de.flowprojects.listeners;

import de.flowprojects.commands.MessageInteractionCommand;
import de.flowprojects.commands.SwipeCommand;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class MessageInteractionEventListener {
    private final static List<MessageInteractionCommand> commands = new ArrayList<>();

    static {
        commands.add(new SwipeCommand());
    }

    public static Mono<Void> handle(MessageInteractionEvent event) {
        Mono<Void> message;

        message = Flux.fromIterable(commands)
                .filter(command -> command.getName().equalsIgnoreCase(event.getCommandName()))
                .next()
                .flatMap(command -> command.handle(event));

        return message;
    }
}
