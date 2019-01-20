package net.sizovs.crf.backbone;

import com.google.common.reflect.TypeToken;

public interface Reaction<C extends Command<R>, R> {

    R react(C $);

    default TypeToken<C> commandType() {
        return new TypeToken<>(getClass()) {
        };
    }

}