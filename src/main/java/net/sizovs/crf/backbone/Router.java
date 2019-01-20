package net.sizovs.crf.backbone;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ForwardingCollection;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Collection;

@Component
class Router {

    private final LoadingCache<Type, Reaction> reactions;

    public Router(AllReactions reactions) {
        this.reactions = Caffeine.newBuilder()
                .build(commandType ->
                        reactions
                                .stream()
                                .filter(reaction -> reaction.commandType().isSupertypeOf(commandType))
                                .findFirst()
                                .orElseThrow(() -> new NoReactionFound(commandType)));
    }

    @SuppressWarnings("unchecked")
    public <C extends Command<R>, R> Reaction<C, R> route(C command) {
        return reactions.get(command.getClass());
    }

    @Component
    static class AllReactions extends ForwardingCollection<Reaction> {

        private final ListableBeanFactory beanFactory;

        public AllReactions(ListableBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        protected Collection<Reaction> delegate() {
            return beanFactory.getBeansOfType(Reaction.class).values();
        }
    }
}