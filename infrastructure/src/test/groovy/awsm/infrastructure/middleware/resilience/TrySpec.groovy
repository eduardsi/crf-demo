package awsm.infrastructure.middleware.resilience

import an.awesome.pipelinr.Command
import an.awesome.pipelinr.Pipeline
import spock.lang.Specification

class TrySpec extends Specification {

    def pipeline = Mock(Pipeline)
    def cmd = Mock(Command)

    def "executes wrapped command only once if it doesn't fail"() {
        when: "I try to execute a command"
            def results = new Try(cmd).execute(pipeline)
        then: "The command doesn't fail and is executed once"
            1 * cmd.execute(pipeline) >> "OK"
        and: "It returns a result"
            results == "OK"
    }

    def "returns if wrapped command fails less than three times"() {
        given: "I have a command that fails two times, then succeeds"
            cmd.execute(pipeline) >> { throw new RuntimeException("💥") } >> { throw new RuntimeException("💥")} >> "OK"
        when: "I try to execute that command"
            def results = new Try(cmd).execute(pipeline)
        then: "It returns a result"
            results == "OK"
    }

    def "throws if wrapped command keeps failing on the third invocation"() {
        given: "I have a command that fails three times in a row"
            cmd.execute(pipeline) >>
                    { throw new RuntimeException("💥") } >>
                    { throw new RuntimeException("💥") } >>
                    { throw new RuntimeException("💥") }
        when: "I try to execute that command"
            new Try(cmd).execute(pipeline)
        then: "I get an error"
            def e = thrown(RuntimeException)
            e.message == "💥"
    }

}
