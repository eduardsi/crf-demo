package awsm.infrastructure.security;

import java.util.Optional;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Encryption {

  private static final Logger logger = LoggerFactory.getLogger(Encryption.class);

  private static final String HARD_CODED_ENCRYPTION_PWD = "12345";

  private static BasicTextEncryptor ENCRYPTOR;

  private Encryption(
      @Value("${security.encryption.password:#{null}}") Optional<String> encryptionPwd) {
    ENCRYPTOR = new BasicTextEncryptor();
    ENCRYPTOR.setPassword(
        encryptionPwd.orElseGet(
            () -> {
              logger.warn(
                  "Encryption is not configured and is not production-ready. Hard-coded encryption password {} will be used",
                  HARD_CODED_ENCRYPTION_PWD);
              return HARD_CODED_ENCRYPTION_PWD;
            }));
  }

  public static String encrypt(String rawValue) {
    return ENCRYPTOR.encrypt(rawValue);
  }

  public static String decrypt(String encValue) {
    return ENCRYPTOR.decrypt(encValue);
  }
}
