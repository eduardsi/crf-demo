package awsm.infra.memoization;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class Memoizers {

  private Memoizers() {
  }

  public static <F, T> Function<F, T> memoized(Function<F, T> nonMemoized) {
    return new FunctionMemoizer<>(nonMemoized)::memoized;
  }

  public static <T> Supplier<T> memoized(Supplier<T> nonMemoized) {
    return new SupplierMemoizer<>(nonMemoized)::memoized;
  }

  private static class FunctionMemoizer<F, T> {

    private final Map<F, T> memoizer = new ConcurrentHashMap<>(1, 1.0f);
    private final Function<F, T> nonMemoized;

    private FunctionMemoizer(Function<F, T> nonMemoized) {
      this.nonMemoized = nonMemoized;
    }

    T memoized(F key) {
      return memoizer.computeIfAbsent(key, nonMemoized);
    }

  }

  private static class SupplierMemoizer<T> {

    private final Map<SupplierMemoizer, T> memoizer = new ConcurrentHashMap<>(1, 1.0f);
    private final Supplier<T> nonMemoized;

    private SupplierMemoizer(Supplier<T> nonMemoized) {
      this.nonMemoized = nonMemoized;
    }

    T memoized() {
      return memoizer.computeIfAbsent(this, key -> nonMemoized.get());
    }

  }

}
