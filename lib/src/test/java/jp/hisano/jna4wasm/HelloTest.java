package jp.hisano.jna4wasm;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class HelloTest {
	@Test
	public void hello() {
		Hello library = Native.load("hello.wasm", Hello.class);
		Assertions.assertThat(library.hello("World")).isEqualTo("Hello, World!");
	}

	public interface Hello extends Library {
		String hello(String name);
	}
}
