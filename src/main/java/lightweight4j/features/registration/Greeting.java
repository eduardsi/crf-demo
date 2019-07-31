package lightweight4j.features.registration;

import an.awesome.pipelinr.Voidy;
import lightweight4j.lib.pipeline.ExecutableCommand;
import lightweight4j.lib.domain.SideEffect;
import org.springframework.stereotype.Component;

public class Greeting implements ExecutableCommand<Voidy> {

    public final Long memberId;

    public Greeting(Long memberId) {
        this.memberId = memberId;
    }

    @Component
    static class ViaSideEffect implements SideEffect<RegistrationCompleted> {

        @Override
        public void completed(RegistrationCompleted event) {
            new Greeting(event.memberId()).execute();
        }

    }
}
