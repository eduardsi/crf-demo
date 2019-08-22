package awsm.infra.middleware.impl.scheduler;

import javax.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
interface ScheduledCommands extends Repository<ScheduledCommand, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Page<ScheduledCommand> findAll(Specification<ScheduledCommand> spec, Pageable page);

  void save(ScheduledCommand scheduledCommand);

}
