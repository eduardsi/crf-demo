package net.sizovs.crf.backbone;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class Future {

    private final Now now;

    public Future(Now now) {
        this.now = now;
    }

    public <R, C extends Command<R>> CompletableFuture<R> schedule(C command) {
        return CompletableFuture.supplyAsync(() -> now.execute(command));
    }
}
