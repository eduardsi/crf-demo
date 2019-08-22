package awsm.util.concurrency;

import com.machinezoo.noexception.Exceptions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Threads {

  private final Synchronizer synchronizer;
  private final ExecutorService pool;
  private final List<CompletableFuture<Void>> tasks = new ArrayList<>();

  public Threads(int threads) {
    this.synchronizer = new Synchronizer(threads);
    this.pool = Executors.newFixedThreadPool(threads);
  }

  public void spinOff(Runnable runnable) {
    var task = CompletableFuture.runAsync(runnable, pool);
    tasks.add(task);
  }

  public void sync() {
    synchronizer.sync();
  }

  public void waitForAll() {
    tasks.stream().collect(new All<>()).join();
  }

  private static class Synchronizer {

    private final CyclicBarrier synchronizer;

    Synchronizer(int threads) {
      this.synchronizer = new CyclicBarrier(threads);
    }

    void sync() {
      Exceptions.sneak().run(synchronizer::await);
    }

  }
}
