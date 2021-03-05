package awsm.infrastructure.scheduling;

import java.util.stream.Stream;
import javax.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

@Component
interface ScheduledCommandRepository extends Repository<ScheduledCommand, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Stream<ScheduledCommand> findAll(Pageable pageable);

  void save(ScheduledCommand command);

  void delete(ScheduledCommand command);
}
