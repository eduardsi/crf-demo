package awsm.commands;

import an.awesome.pipelinr.Command;
import awsm.domain.crm.Customer;
import awsm.domain.crm.CustomerRepository;
import awsm.domain.crm.Uniqueness;
import awsm.infrastructure.pipeline.middlewares.resilience.RateLimit;
import awsm.infrastructure.security.Encryption;
import awsm.infrastructure.validation.Validator;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static awsm.infrastructure.memoize.FunctionMemoizer.memoize;
import static awsm.infrastructure.security.Encryption.encrypt;
import static java.time.Duration.ofSeconds;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

public class RegisterCommand implements Command<RegisterCommand.Response> {
    public final String firstName;
    public final String lastName;
    public final String personalId;
    public final String email;
    RegisterCommand(String firstName, String lastName, String personalId, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.email = email;
    }

    public static class Response {
        public final String personalId;
        Response(String personalId) {
            this.personalId = personalId;
        }
    }

    @Component
    static class Resilience implements RateLimit<RegisterCommand> {
        @Override
        public Bandwidth bandwidth() {
            var maxCallsPerSecond = 50;
            var refillRate = Refill.greedy(10, ofSeconds(1));
            return Bandwidth.classic(maxCallsPerSecond, refillRate);
        }
    }

    @Component
    @Scope(SCOPE_PROTOTYPE)
    static class Handler implements Command.Handler<RegisterCommand, Response> {
        private final CustomerRepository repo;
        private final Uniqueness uniqueness;

        Handler(CustomerRepository repo, Uniqueness uniqueness) {
            this.repo = repo;
            this.uniqueness = memoize(uniqueness::guaranteed)::apply;
        }

        @Override
        public Response handle(RegisterCommand cmd) {
            validate(cmd);

            var customer = new Customer(cmd.personalId, cmd.firstName, cmd.lastName, cmd.email);
            repo.save(customer);

//            curious: never triggers another DB call due to memoization (and prototype scope)
//            uniqueness.guaranteed(cmd.email);
//            uniqueness.guaranteed(cmd.email);
//            uniqueness.guaranteed(cmd.email);

            var encryptedPersonalId = encrypt(cmd.personalId);
            return new Response(encryptedPersonalId);
        }

        private void validate(RegisterCommand cmd) {
            new Validator<>()
                    .with(() -> cmd.firstName,  StringUtils::isNotEmpty, "firstName is missing")
                    .with(() -> cmd.lastName,   StringUtils::isNotEmpty, "lastName is missing")
                    .with(() -> cmd.personalId, StringUtils::isNotEmpty, "personalId is missing")
                    .with(() -> cmd.email,      StringUtils::isNotEmpty, "email is missing", nested ->
                            nested.with(() -> cmd.email, uniqueness::guaranteed, "email is taken")
                    )
                    .check(cmd);
        }


    }

}
