package awsm.infra.middleware.impl.scheduler;

import java.util.Collection;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
interface ScheduledCommands extends Repository<ScheduledCommand, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Collection<ScheduledCommand> findTop10ByTouchedTimesLessThanAndStatus(long touchedTimes, ScheduledCommand.Status status);

  void save(ScheduledCommand scheduledCommand);

}
