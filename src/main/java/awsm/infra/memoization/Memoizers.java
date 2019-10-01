package awsm.infra.memoization;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class Memoizers {

  public static <F, T> FunctionMemoizer<F, T> memoizer(Function<F, T> nonMemoized) {
    return new FunctionMemoizer<>(nonMemoized);
  }

  public static <T> SupplierMemoizer<T> memoizer(Supplier<T> nonMemoized) {
    return new SupplierMemoizer<>(nonMemoized);
  }

  public static class FunctionMemoizer<F, T> {

    private final ConcurrentHashMap<F, T> memoizer = new ConcurrentHashMap<>();
    private final Function<F, T> nonMemoized;

    private FunctionMemoizer(Function<F, T> nonMemoized) {
      this.nonMemoized = nonMemoized;
    }

    public T memoized(F key) {
      return memoizer.computeIfAbsent(key, nonMemoized);
    }

  }

  public static class SupplierMemoizer<T> {

    private final ConcurrentHashMap<SupplierMemoizer, T> memoizer = new ConcurrentHashMap<>();
    private final Supplier<T> nonMemoized;

    private SupplierMemoizer(Supplier<T> nonMemoized) {
      this.nonMemoized = nonMemoized;
    }

    public T memoized() {
      return memoizer.computeIfAbsent(this, key -> nonMemoized.get());
    }

  }
}
