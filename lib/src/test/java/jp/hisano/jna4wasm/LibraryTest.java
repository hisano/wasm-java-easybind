package jp.hisano.jna4wasm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class LibraryTest {
	@Test
	public void testNoLibrary() {
		assertThatExceptionOfType(UnsatisfiedLinkError.class).isThrownBy(() -> Native.load("no.wasm", Library.class));
	}

	@Test
	public void testNoFunction() {
		assertThatExceptionOfType(UnsatisfiedLinkError.class).isThrownBy(() -> {
			Hello hello = Native.load("hello.wasm", Hello.class);
			hello.hello2("World");
		});
	}

	@AfterEach
	public void tearDown() {
		LibraryContext.get().dispose();
	}

	public interface Hello extends Library {
		void hello2(String name);
	}
}
