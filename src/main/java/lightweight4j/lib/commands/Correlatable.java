package lightweight4j.lib.commands;

class Correlatable implements PipelineBehavior {

    private final Ccid ccid;
    private final PipelineBehavior origin;

    public Correlatable(Ccid ccid, PipelineBehavior origin) {
        this.ccid = ccid;
        this.origin = origin;
    }

    @Override
    public <R, C extends Command<R>> R mixIn(C command) {
        try (var stashAutomatically = ccid.storeForLogging()) {
            return origin.mixIn(command);
        }
    }
}