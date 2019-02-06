package lightweight4j.lib.commands;

class Reactions implements Now.Filter {

    private final Router router;

    public Reactions(Router router) {
        this.router = router;
    }

    @Override
    public <R, C extends Command<R>> R process(C command) {
        var reaction = router.route(command);
        return reaction.react(command);
    }
}