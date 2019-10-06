package awsm.infra.middleware.impl.resilience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

import awsm.infra.middleware.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@DisplayName("a try command")
class TryTest {

  @Mock
  Command<String> cmd;

  @BeforeEach
  void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void executes_wrapped_command_only_once_if_it_doesnt_fail() {
    when(cmd.execute())
        .thenReturn("OK");

    var results = new Try<>(cmd).execute();

    verify(cmd, times(1)).execute();
    assertThat(results).isEqualTo("OK");
  }

  @Test
  void returns_if_wrapped_command_fails_less_than_three_times() {
    when(cmd.execute())
        .thenThrow(new RuntimeException("Boom"))
        .thenThrow(new RuntimeException("Boom"))
        .thenReturn("OK");

    var results = new Try<>(cmd).execute();

    verify(cmd, times(3)).execute();
    assertThat(results).isEqualTo("OK");
  }

  @Test
  void throws_if_the_wrapped_command_keeps_failing_on_the_third_invocation() {
    when(cmd.execute())
        .thenThrow(new RuntimeException("Boom"))
        .thenThrow(new RuntimeException("Boom"))
        .thenThrow(new RuntimeException("Boom"));

    var e = assertThrows(RuntimeException.class, () -> new Try<>(cmd).execute());
    assertThat(e).hasMessage("Boom");
    verify(cmd, times(3)).execute();
  }

}