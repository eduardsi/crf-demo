package awsm.banking;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.google.common.base.Preconditions.checkNotNull;

public interface Repository {

    Repository NONE = new None();

    <T> void persist(T entity);

    <T> T findOne(Object id, Class<T> type);

    class None implements Repository {

        @Override
        public <T> void persist(T entity) {
            throw new UnsupportedOperationException("Repository is not configured. Cannot persist entity: " + entity);
        }

        @Override
        public <T> T findOne(Object id, Class<T> type) {
            throw new UnsupportedOperationException("Repository is not configured. Cannot find entity by id : " + id);
        }
    }

    @Component
    class Jpa implements Repository {

        private final EntityManager entityManager;

        Jpa(EntityManager entityManager) {
            this.entityManager = entityManager;
        }

        @Override
        public <T> void persist(T entity) {
            entityManager.persist(entity);
        }

        @Override
        public <T> T findOne(Object id, Class<T> type) {
            var entity = entityManager.find(type, id);
            return checkNotNull(entity, "Entity %s with a ID %s does not exist.", type.getSimpleName(), id);
        }


    }
}
