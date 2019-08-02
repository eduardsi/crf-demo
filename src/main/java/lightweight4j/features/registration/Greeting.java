package lightweight4j.features.registration;

import an.awesome.pipelinr.Voidy;
import lightweight4j.lib.pipeline.ExecutableCommand;
import lightweight4j.features.SideEffect;
import lightweight4j.lib.pipeline.background.Offload;
import lightweight4j.lib.pipeline.tx.Tx;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

public class Greeting extends ExecutableCommand<Voidy> {

    public final Long memberId;

    private Greeting(Long memberId) {
        this.memberId = memberId;
    }

    @Component
    static class WhenRegistrationCompleted implements SideEffect<RegistrationCompleted> {

        @Override
        public void completed(RegistrationCompleted event) {
            new Offload<>(
                    new Tx<>(
                            new Greeting(event.memberId()))).execute();
        }

    }
}
