package net.sizovs.crf.backbone;

import java.util.concurrent.CompletableFuture;

public interface Command<T extends Command.R> {

    interface R {
        class Void implements R {

        }
    }


    default T execute(Now now) {
        return now.execute(this);
    }

    default CompletableFuture<T> schedule(Future future) {
        return future.schedule(this);
    }

}
