package net.sizovs.crf.backbone;

class Correlable implements Now {

    private final CommandCorrelationId correlationId;
    private final Now origin;

    public Correlable(CommandCorrelationId correlationId, Now origin) {
        this.correlationId = correlationId;
        this.origin = origin;
    }

    @Override
    public <R extends Command.R, C extends Command<R>> R execute(C command) {
        correlationId.storeForLogging();
        R response = origin.execute(command);
        correlationId.stashFromLogging();
        return response;
    }
}