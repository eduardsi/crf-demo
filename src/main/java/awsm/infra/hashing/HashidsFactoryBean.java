package awsm.infra.hashing;

import java.util.Optional;
import javax.annotation.Nonnull;
import org.hashids.Hashids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

@Component
class HashidsFactoryBean extends AbstractFactoryBean<Hashids> {

  private static final Logger logger = LoggerFactory.getLogger(HashidsFactoryBean.class);

  private static final String HARD_CODED_SALT = "12345";

  private final String salt;

  public HashidsFactoryBean(@Value("${security.hashids.salt:#{null}}") Optional<String> salt) {
    this.salt = salt.orElseGet(() -> {
      logger.warn("Hashing is not configured and is not production-ready. Hard-coded salt {} will be used", HARD_CODED_SALT);
      return HARD_CODED_SALT;
    });
  }

  @Override
  public Class<?> getObjectType() {
    return Hashids.class;
  }

  @Override
  @Nonnull
  protected Hashids createInstance() {
    return new Hashids(salt, 10);
  }
}
