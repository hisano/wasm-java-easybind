package jp.hisano.wasm.easybind;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class DefineFuctionTest {
	private DefineModule _module;
	private DefineLibrary _library;

	@BeforeEach
	public void setUp() {
		LibraryContext context = LibraryContext.get();

		_module = new DefineModule();
		context.defineModule(_module);

		_library = Native.load("define.wasm", DefineLibrary.class);
	}

	@AfterEach
	public void tearDown() {
		LibraryContext.get().dispose();
	}

	@Test
	public void testCall() {
		_library.call();

		assertThat(_module._isCalled).isTrue();
	}

	@Test
	public void testCallBoolean() {
		assertThat(_library.call_boolean(true)).isTrue();
		assertThat(_library.call_boolean(false)).isFalse();
	}

	@Test
	public void testCallInt() {
		assertThat(_library.call_int(123)).isEqualTo(123);
	}

	@Test
	public void testCallString() {
		assertThat(_library.call_string("hello")).isEqualTo("hello");
	}

	static class DefineModule implements Module {
		boolean _isCalled;

		public void call_java() {
			_isCalled = true;
		}

		public boolean call_boolean_java(boolean value) {
			return value;
		}

		public int call_int_java(int value) {
			return value;
		}

		public String call_string_java(String value) {
			return value;
		}
	}

	interface DefineLibrary extends Library {
		void call();

		boolean call_boolean(boolean value);
		int call_int(int value);

		String call_string(String value);
	}
}
