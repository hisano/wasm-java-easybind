package jp.hisano.wasm.easybind;

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
	public void testCallVoid() {
		_library.call_void();

		assertThat(_module._isCalled).isTrue();
	}

	@Test
	public void testCallTwo() {
		assertThat(_library.call_two(1, 2)).isEqualTo(3);

		assertThat(_library.call_two(Integer.MIN_VALUE, 1)).isEqualTo(Integer.MIN_VALUE + 1);
		assertThat(_library.call_two(Integer.MAX_VALUE, -1)).isEqualTo(Integer.MAX_VALUE - 1);
	}

	@Test
	public void testCallBoolean() {
		assertThat(_library.call_boolean(true)).isTrue();
		assertThat(_library.call_boolean(false)).isFalse();
	}

	@Test
	public void testCallInt() {
		assertThat(_library.call_int(Integer.MIN_VALUE)).isEqualTo(Integer.MIN_VALUE);
		assertThat(_library.call_int(0)).isEqualTo(0);
		assertThat(_library.call_int(Integer.MAX_VALUE)).isEqualTo(Integer.MAX_VALUE);
	}

	@Test
	public void testCallString() {
		assertThat(_library.call_string("hello")).isEqualTo("hello");
	}

	static class DefineModule implements Module {
		boolean _isCalled;

		public void call_void_java() {
			_isCalled = true;
		}

		public int call_two_java(int value0, int value1) {
			return value0 + value1;
		}

		public boolean call_boolean_java(boolean value) {
			return value;
		}

		public Boolean call_Boolean_java(Boolean value) {
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
		void call_void();
		int call_two(int value0, int value1);

		boolean call_boolean(boolean value);
		Boolean call_Boolean(Boolean value);
		int call_int(int value);

		String call_string(String value);
	}
}
