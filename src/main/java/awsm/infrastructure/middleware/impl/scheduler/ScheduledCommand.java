package awsm.infrastructure.middleware.impl.scheduler;

import static jooq.tables.ScheduledCommand.SCHEDULED_COMMAND;
import static com.google.common.base.Preconditions.checkState;
import static com.machinezoo.noexception.Exceptions.sneak;
import static java.time.ZoneOffset.UTC;

import awsm.infrastructure.middleware.Command;
import jooq.tables.records.ScheduledCommandRecord;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

class ScheduledCommand {

  enum Status {
    PENDING, DONE
  }

  @Nullable
  private Long id;

  private int ranTimes;

  @Nullable
  private LocalDateTime lastRunTime;

  private final LocalDateTime creationDate;

  private Status status;

  private Command command;

  ScheduledCommand(Command command) {
    this.creationDate = LocalDateTime.now(UTC);
    this.status = Status.PENDING;
    this.command = command;
  }

  ScheduledCommand(ScheduledCommandRecord record) {
    this.ranTimes = record.getRanTimes();
    this.lastRunTime = record.getLastRunTime();
    this.creationDate = record.getCreationDate();
    this.status = Status.valueOf(record.getStatus());
    this.command = new CommandConverter().convertToEntityAttribute(record.getCommand());
  }

  void saveNew(DataSource dataSource) {
    DSL.using(dataSource, SQLDialect.POSTGRES)
      .insertInto(SCHEDULED_COMMAND,
          SCHEDULED_COMMAND.RAN_TIMES,
          SCHEDULED_COMMAND.LAST_RUN_TIME,
          SCHEDULED_COMMAND.CREATION_DATE,
          SCHEDULED_COMMAND.STATUS,
          SCHEDULED_COMMAND.COMMAND)
      .values(ranTimes, lastRunTime, creationDate, status.name(), new CommandConverter().convertToDatabaseColumn(command))
      .execute();
  }

  CompletableFuture executeIn(Executor executor) {
    checkState(status == Status.PENDING, "Cannot execute work that is not %s", status);
    this.ranTimes++;
    this.lastRunTime = LocalDateTime.now(UTC);
    return CompletableFuture
        .runAsync(() -> this.command.execute(), executor)
        .thenRun(() -> this.status = Status.DONE);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
  }

  private static class CommandConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
      mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
      mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    String convertToDatabaseColumn(Command attribute) {
      return sneak().get(() -> mapper.writeValueAsString(attribute));
    }

    Command convertToEntityAttribute(String command) {
      return sneak().get(() -> mapper.readValue(command, Command.class));
    }
  }

}
