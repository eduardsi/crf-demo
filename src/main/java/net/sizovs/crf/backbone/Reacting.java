package net.sizovs.crf.backbone;

class Reacting implements Now {

    private final Router router;

    public Reacting(Router router) {
        this.router = router;
    }

    @Override
    public <R extends Command.R, C extends Command<R>> R execute(C command) {
        Reaction<C, R> reaction = router.route(command);
        return reaction.react(command);
    }
}