package jp.hisano.jna4wasm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.github.kawamuray.wasmtime.Func;
import io.github.kawamuray.wasmtime.WasmFunctions;
import io.github.kawamuray.wasmtime.WasmValType;
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

	@Test
	public void testDependentReturnFalse() {
		LibraryContext context = LibraryContext.get();

		Func returnFalse = WasmFunctions.wrap(context.getStore(), WasmValType.I32, () -> {
			return 0;
		});
		context.defineFunction("returnFalse", returnFalse);

		TestLib2 testLib2 = Native.load("testlib2.wasm", TestLib2.class);
		assertThat(testLib2.dependentReturnFalse()).isFalse();
	}

	@AfterEach
	public void tearDown() {
		LibraryContext.get().dispose();
	}

	public interface Hello extends Library {
		void hello2(String name);
	}

	public interface TestLib2 extends  Library {
		boolean dependentReturnFalse();
	}
}
