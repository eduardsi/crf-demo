package lightweight4j.infra.modeling;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;

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
                .isEqualTo("DataTest.Bean[a=Hello,b=1960]");

        assertThat(new Bean("", 0).toString())
                .isEqualTo("DataTest.Bean[a=,b=0]");
    }

    static class Bean extends Data {

        @Nullable
        String a;

        @Nullable
        Integer b;

        Bean(@Nullable String a, @Nullable Integer b) {
            this.a = a;
            this.b = b;
        }
    }

}