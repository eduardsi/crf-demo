package lightweight4j.lib.jackson;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// if a class has a single argument constructor,
// its argument needs to be annotated with @JsonProperty("propertyName").
// This is to preserve legacy behavior, see FasterXML/jackson-databind/#1498
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.SOURCE)
public @interface Issue1498 {


}
