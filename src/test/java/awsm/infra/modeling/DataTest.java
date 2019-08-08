package awsm.infra.modeling;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.testing.EqualsTester;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Test;

class DataTest {

  @Test
  void includes_all_fields_in_equals_and_hash_code() {
    new EqualsTester()
            .addEqualityGroup(new Bean("A", 1), new Bean("A", 1))
            .addEqualityGroup(new Bean("A", 2), new Bean("A", 2))
            .addEqualityGroup(new Bean("B", 1), new Bean("B", 1))
            .addEqualityGroup(new Bean(null, null), new Bean(null, null))
            .testEquals();
  }

  @Test
  void includes_all_fields_in_to_string() {
    assertThat(new Bean("Hello", 1960).toString())
            .isEqualTo("DataTest.Bean[one=Hello,another=1960]");

    assertThat(new Bean("", 0).toString())
            .isEqualTo("DataTest.Bean[one=,another=0]");
  }

  static class Bean extends Data {

    @Nullable
    String one;

    @Nullable
    Integer another;

    Bean(@Nullable String one, @Nullable Integer another) {
      this.one = one;
      this.another = another;
    }
  }

}