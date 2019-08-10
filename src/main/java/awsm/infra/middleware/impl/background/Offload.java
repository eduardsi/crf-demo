package awsm.infra.middleware.impl.background;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.impl.react.Reaction;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;

class Offload<R, C extends Command<R>> implements Command<CompletableFuture<R>> {

  private final C origin;

  public Offload(C origin) {
    this.origin = origin;
  }

  @Component
  static class Re<R, C extends Command<R>> implements Reaction<Offload<R, C>, CompletableFuture<R>> {

    private final ExecutorService workers;

    public Re() {
      this.workers = Executors.newWorkStealingPool();
    }

    @Override
    public CompletableFuture<R> react(Offload<R, C> offloadCmd) {
      var origin = offloadCmd.origin;
      var future = CompletableFuture.supplyAsync(origin::execute, workers);
      return future;
    }
  }

}
