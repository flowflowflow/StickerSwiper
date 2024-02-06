package de.flowprojects.listeners;

import de.flowprojects.commands.AddStickerToServerCommand;
import de.flowprojects.commands.IMessageInteractionCommand;
import de.flowprojects.commands.GetStickerImageCommand;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class MessageInteractionEventListener {
    private final static List<IMessageInteractionCommand> commands = new ArrayList<>();

    static {
        commands.add(new GetStickerImageCommand());
        commands.add(new AddStickerToServerCommand());
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
