package lightweight4j.lib.commands;

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


    default CompletableFuture<R> execute(Future future) {
        return future.schedule(this);
    }

}
