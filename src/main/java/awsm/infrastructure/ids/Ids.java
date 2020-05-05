package awsm.infrastructure.ids;

import org.hashids.Hashids;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Ids {

  private static Optional<Hashids> hashids = Optional.empty();

  Ids(Hashids hashids) {
    Ids.hashids = Optional.of(hashids);
  }

  public static long decoded(String hash) {
    var decode = get().decode(hash);
    return decode[0];
  }

  public static String encoded(long id) {
    return get().encode(id);
  }

  public static Hashids get() {
    return hashids.orElseThrow(() -> {
      throw new IllegalStateException("Hashids are not set");
    });
  }


}
