package awsm.infrastructure.middleware.resilience

import an.awesome.pipelinr.Command
import an.awesome.pipelinr.Pipeline
import an.awesome.pipelinr.Voidy
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.atomic.AtomicLong

import static java.util.concurrent.CompletableFuture.allOf
import static java.util.concurrent.CompletableFuture.runAsync
import static java.util.concurrent.Executors.newFixedThreadPool

class ThrottlingSpec extends Specification {

    def pipeline = Mock(Pipeline)

    def "does not allow more than maximum parallel command executions"() {
        given: "Maximum number of parallel command executions has been set"
            def maximum = 2
            def throttling = new Throttling([new Limit(max: maximum) ])

        when: "I run too many commands in parallel"
            def tooMany = maximum + 1
            def pool = newFixedThreadPool(tooMany)
            def command = new Cmd()
            def tasks = (0..tooMany).collect {
                runAsync({ throttling.invoke(command, { command.execute(pipeline) }) }, pool)
            }

        and: "I wait for all of them to complete"
            allOf(tasks as CompletableFuture[]).join()

        then: "I see an error"
            def e = thrown(CompletionException)
            e.cause.message == "Reached the maximum number of permitted concurrent requests ($maximum)"

        and: "The actual number of execution does not exceed the maximum"
            command.executions.get() == maximum
    }

    static class Limit implements RateLimit<Cmd> {
        private int max
        @Override
        int rateLimit() {
             max
        }
    }

    static class Cmd implements Command<Voidy> {
        private executions = new AtomicLong()
        @Override
        Voidy execute(Pipeline pipeline) {
            sleep 1000
            executions.incrementAndGet()
            new Voidy()
        }

    }

}
