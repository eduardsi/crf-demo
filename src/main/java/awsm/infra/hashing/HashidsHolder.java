package awsm.infra.hashing;

import java.util.Optional;
import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@Component
class HashidsHolder {

  private static Optional<Hashids> hashids = Optional.empty();

  HashidsHolder(Hashids hashids) {
    HashidsHolder.hashids = Optional.of(hashids);
  }

  public static Hashids get() {
    return hashids.orElseThrow(() -> {
      throw new IllegalStateException("Hashids are not set");
    });
  }
}
