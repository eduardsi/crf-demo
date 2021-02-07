package awsm.infrastructure.scheduling;

import static java.time.ZoneOffset.UTC;
import static jooq.tables.ScheduledCommand.SCHEDULED_COMMAND;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import jooq.tables.records.ScheduledCommandRecord;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
class ScheduledCommand {
  private final LocalDateTime creationDate;
  private final Command command;
  private Optional<Long> id = Optional.empty();
  ScheduledCommand(Command command) {
    this(LocalDateTime.now(UTC), command);
  }

  private ScheduledCommand(LocalDateTime creationDate, Command command) {
    this.creationDate = creationDate;
    this.command = command;
  }

  void execute(Pipeline pipeline) {
    command.execute(pipeline);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
  }

  @Component
  static class Repository {

    private final DSLContext dsl;
    private final Gson gson;

    Repository(DSLContext dsl, Gson gson) {
      this.dsl = dsl;
      this.gson = gson;
    }

    Stream<ScheduledCommand> list(long limit) {
      return dsl
              .selectFrom(SCHEDULED_COMMAND)
              .limit(limit)
              .forUpdate()
              .fetchStream()
              .map(fromJooq());
    }

    private Function<ScheduledCommandRecord, ScheduledCommand> fromJooq() {
      return jooq -> {
        var cmd = gson.fromJson(jooq.command(), Command.class);
        var self = new ScheduledCommand(jooq.creationDate(), cmd);
        self.id = Optional.of(jooq.id());
        return self;
      };
    }

    void insert(ScheduledCommand self) {
      var id = dsl
          .insertInto(SCHEDULED_COMMAND)
            .set(SCHEDULED_COMMAND.CREATION_DATE, self.creationDate)
            .set(SCHEDULED_COMMAND.COMMAND, gson.toJson(self.command, Command.class))
            .returning(SCHEDULED_COMMAND.ID)
            .fetchOne()
            .id();
      self.id = Optional.of(id);
    }

    void delete(ScheduledCommand self) {
      dsl
          .deleteFrom(SCHEDULED_COMMAND)
          .where(SCHEDULED_COMMAND.ID.equal(self.id.orElseThrow()))
          .execute();
    }
  }

}
