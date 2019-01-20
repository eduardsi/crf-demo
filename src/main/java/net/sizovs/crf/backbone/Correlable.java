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
        ccid.storeForLogging();
        var response = origin.execute(command);
        ccid.stashFromLogging();
        return response;
    }
}