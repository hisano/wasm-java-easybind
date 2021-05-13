package jp.hisano.jna4wasm;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class AddTest {
	@Test
	public void add() {
		Add add = Native.load("add.wasm", Add.class);
		Assertions.assertThat(add.add(1, 2)).isEqualTo(3);
	}

	public interface Add extends Library {
		int add(int a, int b);
	}
}
