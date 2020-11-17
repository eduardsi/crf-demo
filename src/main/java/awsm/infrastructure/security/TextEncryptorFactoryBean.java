package awsm.infrastructure.security;

import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.TextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Optional;

@Component
class TextEncryptorFactoryBean extends AbstractFactoryBean<TextEncryptor> {

  private static final Logger logger = LoggerFactory.getLogger(TextEncryptorFactoryBean.class);

  private static final String HARD_CODED_ENCRYPTION_PWD = "12345";

  private final String encryptionPwd;

  public TextEncryptorFactoryBean(@Value("${security.encryption.password:#{null}}") Optional<String> encryptionPwd) {
    this.encryptionPwd = encryptionPwd.orElseGet(() -> {
      logger.warn("Encryption is not configured and is not production-ready. Hard-coded encryption password {} will be used", HARD_CODED_ENCRYPTION_PWD);
      return HARD_CODED_ENCRYPTION_PWD;
    });
  }

  @Override
  public Class<?> getObjectType() {
    return TextEncryptor.class;
  }

  @Override
  @Nonnull
  protected TextEncryptor createInstance() {
    var encryptor = new BasicTextEncryptor();
    encryptor.setPassword(encryptionPwd);
    return encryptor;
  }
}