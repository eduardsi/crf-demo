package lightweight4j.features.registration;

import java.util.function.Supplier;

public class RegistrationCompleted {

    private final Supplier<Long> memberId;

    public RegistrationCompleted(Supplier<Long> memberId) {
        this.memberId = memberId;
    }

    public Long memberId() {
        return memberId.get();
    }
}
