package net.sizovs.crf.backbone;

class Reacting implements Now {

    private final Router router;

    public Reacting(Router router) {
        this.router = router;
    }

    @Override
    public <R, C extends Command<R>> R execute(C command) {
        var reaction = router.route(command);
        return reaction.react(command);
    }
}