package awsm.infrastructure.pipeline.ratelimit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
class RateLimitException extends RuntimeException {
    RateLimitException() {
        super("Rate limit has been reached");
    }
}
