package lightweight4j.lib.commands;

class LogCorrelation implements Now.Filter {

    private final Ccid ccid;
    private final Now.Filter origin;

    public LogCorrelation(Ccid ccid, Now.Filter origin) {
        this.ccid = ccid;
        this.origin = origin;
    }

    @Override
    public <R, C extends Command<R>> R process(C command) {
        try (var stashAutomatically = ccid.storeForLogging()) {
            return origin.process(command);
        }
    }
}