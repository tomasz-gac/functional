package functional.com.tgac.functional;
import com.tgac.functional.Functions;
import com.tgac.functional.Numbers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.tgac.functional.Functions.function;
public class FunctionsTest {
	@Test
	public void shouldBuildFunctions(){
		Assertions.assertThat(function(() -> "").apply())
				.isEqualTo("");
		Assertions.assertThat(function(Object::toString).apply(1))
				.isEqualTo("1");
		Assertions.assertThat(function((a, b) -> a.toString()+b).apply(1, 2))
				.isEqualTo("12");
		Assertions.assertThat(function((a, b, c) -> a.toString()+b+c).apply(1, 2, 3))
				.isEqualTo("123");
	}

	@Test
	public void shouldApplyPartial(){
		Assertions.assertThat(function((a, b, c) -> a.toString()+b+c)
				.partial(1)
				.apply(2, 3))
				.isEqualTo("123");
	}

	@Test
	public void shouldApplyPartialRight(){
		Assertions.assertThat(function((a, b, c) -> a.toString()+b+c)
				.partialRight(3)
				.apply(1, 2))
				.isEqualTo("123");
	}

	@Test
	public void shouldApplyPartialByNumber(){
		Assertions.assertThat(function((a, b, c) -> a.toString()+b+c)
				.partial(Numbers._1(), 2)
				.apply(1, 3))
				.isEqualTo("123");
	}
}
