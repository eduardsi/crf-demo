package net.sizovs.crf.backbone;

class Correlable implements Now {

    private final Ccid ccid;
    private final Now origin;

    public Correlable(Ccid ccid, Now origin) {
        this.ccid = ccid;
        this.origin = origin;
    }

    @Override
    public <R, C extends Command<R>> R execute(C command) {
        try (var stashAutomatically = ccid.storeForLogging()) {
            return origin.execute(command);
        }
    }
}