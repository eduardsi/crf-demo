package lightweight4j.features.membership.impl;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailBlacklist {

    private static final List<String> BAD_DOMAINS = List.of("pornhub.com", "rotten.com");

    public boolean contains(Email email) {
        return BAD_DOMAINS
                .stream()
                .anyMatch(domain -> email.toString().contains(domain));
    }

}
