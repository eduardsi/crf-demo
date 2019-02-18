package lightweight4j.lib.pipeline;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.PipelineStep;
import an.awesome.pipelinr.Pipelinr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Configuration
class PipelineConfiguration {

    @Bean
    Pipeline pipeline(ObjectProvider<Command.Handler> commandHandlers, ObjectProvider<PipelineStep> pipelineSteps) {
        return new Pipelinr(commandHandlers::stream, pipelineSteps::orderedStream);
    }

}

@Component
@Order(1)
class CorrelateLogs implements PipelineStep {

    private final CorrelationId correlationId;

    @Autowired
    CorrelateLogs(CorrelationId correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        try (var stashAutomatically = correlationId.storeForLogging()) {
            return next.invoke();
        }
    }
}

@Component
@Order(2)
class LogInputAndOutput implements PipelineStep {


    private final Logger log = LoggerFactory.getLogger(LogInputAndOutput.class);

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        log.info(">>> {}", command.toString());
        var response = next.invoke();
        log.info("<<< {} ", response.toString());
        return response;
    }

}

@Component
@Order(3)
class WrapInTx implements PipelineStep {

    private final TransactionTemplate tx;

    @Autowired
    WrapInTx(PlatformTransactionManager txManager) {
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        return tx.execute(txStatus -> next.invoke());
    }
}

@Component
@Order(4)
class Validate implements PipelineStep {

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        var violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new CommandValidationException(violations);
        }

        return next.invoke();

    }
}