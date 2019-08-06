package lightweight4j.domain.registration;

import lightweight4j.infra.modeling.Event;

import java.util.function.Supplier;

public class RegistrationCompleted implements Event {

    private final Supplier<Long> memberId;

    RegistrationCompleted(Supplier<Long> memberId) {
        this.memberId = memberId;
    }

    public Long memberId() {
        return memberId.get();
    }
}
