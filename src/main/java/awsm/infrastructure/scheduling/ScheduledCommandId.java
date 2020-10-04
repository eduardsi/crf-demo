package awsm.infrastructure.scheduling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

@Component
@Lazy
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ScheduledCommandId {

  @AliasFor(annotation = Component.class)
  String value() default "";

}
