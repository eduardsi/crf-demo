package awsm.infra.middleware;

import static java.lang.String.format;

public interface Middleware {

  <R, C extends Command<R>> R invoke(C command, Next<R> next);

  @FunctionalInterface
  interface Next<T> {

    T invoke();

    class Null<R> implements Next<R> {

      @Override
      public R invoke() {
        var msg  = format(
                "The %s of the last middleware is a null object and should not be invoked.",
                Next.class.getSimpleName());

        throw new IllegalStateException(msg);
      }
    }
  }

}
