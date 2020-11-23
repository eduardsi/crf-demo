package awsm.infrastructure.pipeline.ratelimit;

import io.github.bucket4j.Bandwidth;

public interface RateLimited {
  Bandwidth bandwidth();
}