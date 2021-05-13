package jp.hisano.jna4wasm;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class LibraryLoadTest {
	@Test
	public void testNoLibrary() {
		assertThatExceptionOfType(UnsatisfiedLinkError.class).isThrownBy(() -> Native.load("no.wasm", Library.class));
	}
}
