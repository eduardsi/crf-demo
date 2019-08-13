package awsm.infra.middleware.impl.scheduler;

import static com.google.common.base.Preconditions.checkState;
import static java.time.ZoneOffset.UTC;

import awsm.infra.hibernate.HibernateConstructor;
import awsm.infra.middleware.Command;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
class ScheduledCommand {

  enum Status {
    PENDING, DONE
  }

  @Nullable
  @GeneratedValue
  @Id
  private Long id;

  @Column
  private long touchedTimes;

  @Column
  private LocalDateTime createdAt = LocalDateTime.now(UTC);

  @Column
  @Nullable
  private LocalDateTime touchedAt;

  @Enumerated(EnumType.STRING)
  private Status status = Status.PENDING;

  @Convert(converter = CommandConverter.class)
  private Command command;

  ScheduledCommand(Command command) {
    this.command = command;
  }

  @HibernateConstructor
  private ScheduledCommand() {
  }

  CompletableFuture executeIn(Executor executor) {
    checkState(status == Status.PENDING, "Cannot execute work that is not %s", status);
    this.touchedTimes++;
    this.touchedAt = LocalDateTime.now(UTC);
    return CompletableFuture
        .runAsync(() -> this.command.execute(), executor)
        .thenRun(() -> this.status = Status.DONE);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
  }

}
