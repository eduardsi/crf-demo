package awsm.infrastructure.middleware.impl.scheduler;

import static com.google.common.base.Preconditions.checkState;
import static com.machinezoo.noexception.Exceptions.sneak;
import static java.time.ZoneOffset.UTC;

import awsm.infrastructure.middleware.Command;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.jdbc.core.JdbcTemplate;

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

  ScheduledCommand(ResultSet rs) throws SQLException {
    this.ranTimes = rs.getInt("ran_times");
    this.lastRunTime = rs.getTimestamp("last_run_time") != null ? rs.getTimestamp("last_run_time").toLocalDateTime() : null;
    this.creationDate = rs.getTimestamp("creation_date").toLocalDateTime();
    this.status = Status.valueOf(rs.getString("status"));
    this.command = new CommandConverter().convertToEntityAttribute(rs.getString("command"));
  }
  void saveNew(JdbcTemplate jdbcTemplate) {
    jdbcTemplate.update(
        """
              INSERT INTO scheduled_command
              (ran_times, last_run_time, creation_date, status, command) VALUES
              (?, ?, ?, ?, ?)
          """,
        ranTimes, lastRunTime, creationDate, status.name(), new CommandConverter().convertToDatabaseColumn(command));
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
