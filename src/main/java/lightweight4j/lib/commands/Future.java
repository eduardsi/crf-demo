package lightweight4j.lib.commands;

import java.util.concurrent.CompletableFuture;

public interface Future {

    <R, C extends Command<R>> CompletableFuture<R> schedule(C command);

}
