package awsm.application;

import an.awesome.pipelinr.Command;
import awsm.domain.crm.Customer;
import awsm.domain.crm.CustomerRepository;
import awsm.domain.crm.UniqueEmail;
import awsm.domain.crm.Uniqueness;
import awsm.infrastructure.pipeline.ratelimit.RateLimited;
import awsm.infrastructure.validation.Validator;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static awsm.infrastructure.memoize.FunctionMemoizer.memoize;
import static java.time.Duration.ofSeconds;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

public class Register implements Command<Register.Response>, RateLimited {

    private final String firstName;
    private final String lastName;
    private final String personalId;
    private final String email;

    Register(String firstName, String lastName, String personalId, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.email = email;
    }

    @Override
    public Bandwidth bandwidth() {
        var maxCallsPerSecond = 50;

        // Tokens in the bucket increase at a refill rate of 10 calls per second.
        // So, if request rate is 10 calls per second, it will never be throttled.
        var refillRate = Refill.greedy(10, ofSeconds(1));
        return Bandwidth.classic(maxCallsPerSecond, refillRate);
    }

    public static class Response {
        public final String personalId;
        Response(String personalId) {
            this.personalId = personalId;
        }
    }

    @Scope(SCOPE_PROTOTYPE)
    @Component
    static class Handler implements Command.Handler<Register, Response> {

        private final TextEncryptor textEncryptor;

        private final CustomerRepository repo;

        private final Uniqueness uniqueness;

        Handler(TextEncryptor textEncryptor, CustomerRepository repo, Uniqueness uniqueness) {
            this.textEncryptor = textEncryptor;
            this.repo = repo;
            this.uniqueness = memoize(uniqueness::guaranteed)::apply;
        }

        @Override
        public Response handle(Register command) {
            validate(command);

            var email = new UniqueEmail(command.email, uniqueness);
            var customer = new Customer(command.personalId, command.firstName, command.lastName, email);
            customer.register(repo);

            return new Response(encrypted(command.personalId));
        }

        private void validate(Register command) {
            new Validator<>()
                    .with(() -> command.firstName, v -> !v.isBlank(), "firstName is missing")
                    .with(() -> command.lastName, v -> !v.isBlank(), "lastName is missing")
                    .with(() -> command.personalId, v -> !v.isBlank(), "personalId is missing")
                    .with(() -> command.email, v -> !v.isBlank(), "email is missing", nested ->
                            nested.with(() -> command.email, (email) -> uniqueness.guaranteed(email), "email is taken")
                    )
                    .check(command);
        }

        private String encrypted(String personalId) {
            return textEncryptor.encrypt(personalId);
        }

    }

}
