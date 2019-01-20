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

    default CompletableFuture<R> schedule(Future future) {
        return future.schedule(this);
    }

}
