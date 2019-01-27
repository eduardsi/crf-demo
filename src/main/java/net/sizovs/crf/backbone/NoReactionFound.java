package net.sizovs.crf.backbone;

public class NoReactionFound extends RuntimeException {

    private final Command command;

    public NoReactionFound(Command command) {
        this.command = command;
    }

    @Override
    public String getMessage() {
        return "Cannot find reaction for command " + command;
    }
}
