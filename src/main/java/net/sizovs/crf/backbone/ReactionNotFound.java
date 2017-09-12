package net.sizovs.crf.backbone;

import java.lang.reflect.Type;

public class ReactionNotFound extends RuntimeException {

    private final Type commandType;

    public ReactionNotFound(Type commandType) {
        this.commandType = commandType;
    }

    @Override
    public String getMessage() {
        return "Cannot find reaction for command " + commandType;
    }
}
