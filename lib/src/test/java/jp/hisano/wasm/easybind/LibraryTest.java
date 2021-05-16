package jp.hisano.wasm.easybind;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class LibraryTest {
	@AfterEach
	public void tearDown() {
		LibraryContext.get().dispose();
	}

	@Test
	public void testAdd() {
		Add add = Native.load("add.wasm", Add.class);
		assertThat(add.add(1, 2)).isEqualTo(3);
	}

	public interface Add extends Library {
		int add(int a, int b);
	}

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

	public interface Hello extends Library {
		void hello2(String name);
	}

	@Test
	public void testDependentReturnFalse() {
		LibraryContext context = LibraryContext.get();
		context.defineModule(new TestLib2Module());
		TestLib2 testLib2 = Native.load("testlib2.wasm", TestLib2.class);
		assertThat(testLib2.dependentReturnFalse()).isEqualTo(0);
	}

	public interface TestLib2 extends Library {
		int dependentReturnFalse();
	}

	private class TestLib2Module implements Module {
		public int returnFalse() {
			return 0;
		}
	}
}
