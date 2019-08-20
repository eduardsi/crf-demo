package awsm.infra.middleware.impl.react.validation;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Validator<R> {

  private final List<Rule<R>> rules = new ArrayList<>();

  public <T> Validator<R> with(AttributeGetter<T> getter, AttributeCheck<T> condition, String message) {
    return with(getter, condition, message, new Nesting.Absent<>());
  }

  public <T> Validator<R> with(AttributeGetter<T> getter, AttributeCheck<T> check, String message, Nesting<R> nested) {
    var rule = new AttributeRule<>(getter, check, value -> format(message, value));
    rule.with(nested.validator());
    rules.add(rule);
    return this;
  }

  public void check(R root) {
    var violations = validate(root);
    if (!violations.isEmpty()) {
      throw new ValidationException(violations);
    }
  }

  private List<String> validate(R root) {
    return rules
        .stream()
        .flatMap(constraint -> constraint.violations(root).stream())
        .collect(Collectors.toList());
  }

  public interface Nesting<R> {
    class Absent<T> implements Nesting<T> {
      @Override
      public void applyTo(Validator<T> nested) {
      }
    }

    void applyTo(Validator<R> validator);

    default Validator<R> validator() {
      var validator = new Validator<R>();
      applyTo(validator);
      return validator;
    }
  }

  @FunctionalInterface
  public interface AttributeGetter<T> {
    T attr();
  }

  public interface AttributeCheck<T> {
    boolean isTruthy(T attribute);
  }

  public interface AttributeViolation<T> {
    String text(T value);
  }

  private interface Rule<R> {
    Collection<String> violations(R entity);
  }

  private class AttributeRule<V> implements Rule<R> {
    private AttributeGetter<V> getter;
    private AttributeCheck<V> check;
    private AttributeViolation<V> violation;
    private Validator<R> nestedValidator = new Validator<>();

    AttributeRule(AttributeGetter<V> getter, AttributeCheck<V> check, AttributeViolation<V> violation)  {
      this.getter = getter;
      this.check = check;
      this.violation = violation;
    }

    void with(Validator<R> validator) {
      this.nestedValidator = validator;
    }

    @Override
    public Collection<String> violations(R root) {
      var attr = this.getter.attr();
      var truthy = this.check.isTruthy(attr);
      if (!truthy) {
        return singletonList(violation.text(attr));
      } else {
        return this.nestedValidator.validate(root);
      }
    }

  }
}
