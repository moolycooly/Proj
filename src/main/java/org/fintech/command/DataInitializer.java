package org.fintech.command;

import lombok.RequiredArgsConstructor;
import org.fintech.command.commands.DataInitCommand;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final List<DataInitCommand> commands;
    public void addCommand(DataInitCommand command) {
        commands.add(command);
    }
    public void execute() {
        for (DataInitCommand command : commands) {
            command.execute();
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        commands.forEach(DataInitCommand::execute);
    }
}
