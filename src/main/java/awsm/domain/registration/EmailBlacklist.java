package awsm.domain.registration;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EmailBlacklist {

  private static final List<String> BAD_DOMAINS = List.of("pornhub.com", "rotten.com");

  public boolean contains(Email email) {
    return BAD_DOMAINS
            .stream()
            .anyMatch(domain -> email.toString().contains(domain));
  }

}
