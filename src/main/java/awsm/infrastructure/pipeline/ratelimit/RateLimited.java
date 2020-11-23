package awsm.infrastructure.pipeline.ratelimit;

import com.github.bucket4j.Bandwidth;

public interface RateLimited {

  Bandwidth bandwidth();

}