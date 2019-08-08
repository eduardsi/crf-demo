package awsm.infra.pipeline.background;

import an.awesome.pipelinr.Command;
import awsm.infra.pipeline.ExecutableCommand;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;

class Offload<R, C extends ExecutableCommand<R>> extends ExecutableCommand<CompletableFuture<R>> {

  private final C origin;

  public Offload(C origin) {
    this.origin = origin;
  }

  @Component
  static class Handler<R, C extends ExecutableCommand<R>> implements Command.Handler<Offload<R, C>, CompletableFuture<R>> {

    private final ExecutorService workers;

    public Handler() {
      this.workers = Executors.newWorkStealingPool();
    }

    @Override
    public CompletableFuture<R> handle(Offload<R, C> offloadCmd) {
      var origin = offloadCmd.origin;
      var future = CompletableFuture.supplyAsync(origin::execute, workers);
      return future;
    }
  }

}
