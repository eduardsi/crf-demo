package awsm.infrastructure.validation;

import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> violations;

    ValidationException(List<String> violations) {
        this.violations = violations;
    }

    public List<String> violations() {
        return violations;
    }

    @Override
    public String getMessage() {
        return String.join(", ", violations);
    }

}
