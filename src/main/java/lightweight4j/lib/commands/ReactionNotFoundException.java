package lightweight4j.lib.commands;

public class ReactionNotFoundException extends RuntimeException {

    private final Command command;

    public ReactionNotFoundException(Command command) {
        this.command = command;
    }

    @Override
    public String getMessage() {
        return "Cannot find reaction for command " + command;
    }
}
