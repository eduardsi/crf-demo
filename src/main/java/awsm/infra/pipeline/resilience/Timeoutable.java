package awsm.infra.pipeline.resilience;

import java.time.Duration;

public interface Timeoutable {

  Duration maxDuration();

}
