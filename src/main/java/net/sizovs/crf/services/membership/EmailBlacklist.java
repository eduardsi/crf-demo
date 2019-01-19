package net.sizovs.crf.services.membership;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EmailBlacklist {

    private final List<String> badDomains = Arrays.asList("pornhub.com", "rotten.com");

    public boolean contains(Email email) {
        return badDomains.stream().anyMatch(domain -> email.toString().contains(domain));
    }

}
