package lightweight4j.lib.commands;

class Reacting implements PipelineBehavior {

    private final Router router;

    public Reacting(Router router) {
        this.router = router;
    }

    @Override
    public <R, C extends Command<R>> R mixIn(C command) {
        var reaction = router.route(command);
        return reaction.react(command);
    }
}