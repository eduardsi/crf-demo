package awsm.util.concurrency;

import static java.util.stream.Collectors.toList;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class All<T> implements Collector<CompletableFuture<T>, List<CompletableFuture<T>>, CompletableFuture<List<T>>> {

  @Override
  public Supplier<List<CompletableFuture<T>>> supplier() {
    return ArrayList::new;
  }

  @Override
  public BiConsumer<List<CompletableFuture<T>>, CompletableFuture<T>> accumulator() {
    return Collection::add;
  }

  @Override
  public BinaryOperator<List<CompletableFuture<T>>> combiner() {
    return (one, another) -> ImmutableList.<CompletableFuture<T>>builder()
        .addAll(one)
        .addAll(another)
        .build();
  }

  @Override
  public Function<List<CompletableFuture<T>>, CompletableFuture<List<T>>> finisher() {
    return futures ->
        CompletableFuture
            .allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(toList()));
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Collections.singleton(Characteristics.CONCURRENT);
  }
}

