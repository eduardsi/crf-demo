package awsm.infrastructure.middleware.impl.scheduler;

import static awsm.infrastructure.middleware.impl.scheduler.ScheduledCommand.Status.PENDING;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING;
import static com.google.common.base.Preconditions.checkState;
import static com.machinezoo.noexception.Exceptions.sneak;
import static java.time.ZoneOffset.UTC;
import static jooq.tables.ScheduledCommand.SCHEDULED_COMMAND;

import awsm.infrastructure.middleware.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import jooq.tables.records.ScheduledCommandRecord;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

class ScheduledCommand {

  enum Status {
    PENDING, DONE
  }

  private static final ObjectMapper mapper = JsonMapper
      .builder()
      .activateDefaultTyping(BasicPolymorphicTypeValidator
          .builder()
          .allowIfBaseType(Command.class)
          .build(), EVERYTHING)
      .visibility(FIELD, ANY)
      .build();


  private final LocalDateTime creationDate;

  private final Command command;

  private int ranTimes;

  @Nullable
  private LocalDateTime lastRunTime;

  private Status status = Status.PENDING;

  ScheduledCommand(Command command) {
    this(LocalDateTime.now(UTC), command);
  }

  private ScheduledCommand(LocalDateTime creationDate, Command command) {
    this.creationDate = creationDate;
    this.command = command;
  }

  CompletableFuture<ScheduledCommand> executeIn(Executor executor) {
    checkState(status == Status.PENDING, "Cannot execute work that is not %s", status);
    this.ranTimes++;
    this.lastRunTime = LocalDateTime.now(UTC);
    return CompletableFuture
        .supplyAsync(() -> {
          this.command.execute();
          return this;
        }, executor)
        .thenApply(it -> {
          this.status = Status.DONE;
          return this;
        });
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
  }

  @Component
  static class Repository {

    private final DSLContext dsl;

    Repository(DSLContext dsl) {
      this.dsl = dsl;
    }

    Stream<ScheduledCommand> batchOfPending(long limit) {
      return dsl
          .selectFrom(SCHEDULED_COMMAND)
          .where(SCHEDULED_COMMAND.RAN_TIMES.lessThan(3), SCHEDULED_COMMAND.STATUS.eq(PENDING.name()))
          .limit(limit)
          .forUpdate()
          .fetchStream()
          .map(fromJooq());
    }

    private Function<ScheduledCommandRecord, ScheduledCommand> fromJooq() {
      return jooq -> {
        var cmd = sneak().get(() -> mapper.readValue(jooq.getCommand(), Command.class));
        var self = new ScheduledCommand(jooq.getCreationDate(), cmd);
        self.ranTimes = jooq.getRanTimes();
        self.lastRunTime = jooq.getLastRunTime();
        self.status = Status.valueOf(jooq.getStatus());
        return self;
      };
    }

    void insert(ScheduledCommand self) {
      dsl
          .insertInto(SCHEDULED_COMMAND)
            .set(SCHEDULED_COMMAND.RAN_TIMES, self.ranTimes)
            .set(SCHEDULED_COMMAND.LAST_RUN_TIME, self.lastRunTime)
            .set(SCHEDULED_COMMAND.CREATION_DATE, self.creationDate)
            .set(SCHEDULED_COMMAND.STATUS, self.status.name())
            .set(SCHEDULED_COMMAND.COMMAND, sneak().get(() -> mapper.writeValueAsString(self.command)))
            .execute();
    }

    void update(ScheduledCommand self) {
      dsl
          .update(SCHEDULED_COMMAND)
          .set(SCHEDULED_COMMAND.RAN_TIMES, self.ranTimes)
          .set(SCHEDULED_COMMAND.LAST_RUN_TIME, self.lastRunTime)
          .set(SCHEDULED_COMMAND.STATUS, self.status.name())
          .execute();
    }
  }

}
