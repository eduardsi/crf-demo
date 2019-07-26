package lightweight4j.features.membership;

import lightweight4j.lib.domain.DomainEvent;

import java.util.function.Supplier;

class MemberHasArrived implements DomainEvent {

    private final Supplier<Long> memberId;

    MemberHasArrived(Supplier<Long> memberId) {
        this.memberId = memberId;
    }

    Long memberId() {
        return memberId.get();
    }
}
