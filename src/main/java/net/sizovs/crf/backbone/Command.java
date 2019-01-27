package net.sizovs.crf.backbone;

import java.util.concurrent.CompletableFuture;

public interface Command<R> {

    class Void {
        @Override
        public String toString() {
            return "Void";
        }
    }

    default R execute(Now now) {
        return now.execute(this);
    }

    default void execute(Eventually eventually) {
        eventually.execute(this);
    }

    default CompletableFuture<R> execute(Future async) {
        return async.execute(this);
    }

}
