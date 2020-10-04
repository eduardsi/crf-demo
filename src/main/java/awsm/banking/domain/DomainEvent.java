package awsm.banking.domain;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

public interface DomainEvent {

    default Type type() {
        return this.getClass();
    }


    interface SideEffect<T extends DomainEvent> {

        void trigger(T event);

        default TypeToken<T> eventType() {
            return new TypeToken<>(getClass()) {
            };
        }
    }



}
