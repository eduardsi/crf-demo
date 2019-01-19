package net.sizovs.crf.services.core;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.springframework.core.annotation.AnnotatedElementUtils.hasAnnotation;

@Component
class HibernateConfiguration implements HibernatePropertiesCustomizer {


    private final AutowireOnLoad autowireOnLoad;

    public HibernateConfiguration(AutowireOnLoad autowireOnLoad) {
        this.autowireOnLoad = autowireOnLoad;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.interceptor", autowireOnLoad);
    }
}


@Component
class AutowireOnLoad extends EmptyInterceptor {

    private final static boolean STATE_MODIFICATIONS_HAVE_BEEN_MADE = false;

    private final AutowireCapableBeanFactory spring;

    public AutowireOnLoad(AutowireCapableBeanFactory spring) {
        this.spring = spring;
    }

    @Override
    public boolean onLoad(Object root, Serializable id, Object[] dependencies, String[] props, Type[] types) {
        Arrays.stream(dependencies)
                .filter(this::forAutowiring)
                .forEach(this::autowire);

        autowire(root);

        return STATE_MODIFICATIONS_HAVE_BEEN_MADE;
    }

    private boolean forAutowiring(Object dependency) {
        var annotations = asList(Entity.class, Embeddable.class);
        return annotations.stream().anyMatch(annotation -> hasAnnotation(dependency.getClass(), annotation));
    }

    private void autowire(Object dependency) {
        spring.autowireBeanProperties(dependency, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }


}
