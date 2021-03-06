package jp.hisano.wasm.easybind;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class DefineFunctionTest {
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

		assertThat(_library.call_Boolean(true)).isTrue();
		assertThat(_library.call_Boolean(false)).isFalse();
	}

	@Test
	public void testCallByte() {
		assertThat(_library.call_byte(Byte.MIN_VALUE)).isEqualTo(Byte.MIN_VALUE);
		assertThat(_library.call_byte((byte) 0)).isEqualTo((byte) 0);
		assertThat(_library.call_byte(Byte.MAX_VALUE)).isEqualTo(Byte.MAX_VALUE);

		assertThat(_library.call_Byte(Byte.MIN_VALUE)).isEqualTo(Byte.MIN_VALUE);
		assertThat(_library.call_Byte((byte) 0)).isEqualTo((byte) 0);
		assertThat(_library.call_Byte(Byte.MAX_VALUE)).isEqualTo(Byte.MAX_VALUE);
	}

	@Test
	public void testCallShort() {
		assertThat(_library.call_short(Short.MIN_VALUE)).isEqualTo(Short.MIN_VALUE);
		assertThat(_library.call_short((short) 0)).isEqualTo((short) 0);
		assertThat(_library.call_short(Short.MAX_VALUE)).isEqualTo(Short.MAX_VALUE);

		assertThat(_library.call_Short(Short.MIN_VALUE)).isEqualTo(Short.MIN_VALUE);
		assertThat(_library.call_Short((short) 0)).isEqualTo((short) 0);
		assertThat(_library.call_Short(Short.MAX_VALUE)).isEqualTo(Short.MAX_VALUE);
	}

	@Test
	public void testCallChar() {
		assertThat(_library.call_char('a')).isEqualTo('a');
		assertThat(_library.call_char('A')).isEqualTo('A');

		assertThat(_library.call_Character(Character.valueOf('a'))).isEqualTo('a');
		assertThat(_library.call_Character(Character.valueOf('A'))).isEqualTo('A');
	}

	@Test
	public void testCallInt() {
		assertThat(_library.call_int(Integer.MIN_VALUE)).isEqualTo(Integer.MIN_VALUE);
		assertThat(_library.call_int(0)).isEqualTo(0);
		assertThat(_library.call_int(Integer.MAX_VALUE)).isEqualTo(Integer.MAX_VALUE);

		assertThat(_library.call_Integer(Integer.MIN_VALUE)).isEqualTo(Integer.MIN_VALUE);
		assertThat(_library.call_Integer(0)).isEqualTo(0);
		assertThat(_library.call_Integer(Integer.MAX_VALUE)).isEqualTo(Integer.MAX_VALUE);
	}

	@Test
	public void testCallLong() {
		assertThat(_library.call_long(Long.MIN_VALUE)).isEqualTo(Long.MIN_VALUE);
		assertThat(_library.call_long(0L)).isEqualTo(0L);
		assertThat(_library.call_long(Long.MAX_VALUE)).isEqualTo(Long.MAX_VALUE);

		assertThat(_library.call_Long(Long.MIN_VALUE)).isEqualTo(Long.MIN_VALUE);
		assertThat(_library.call_Long(0L)).isEqualTo(0L);
		assertThat(_library.call_Long(Long.MAX_VALUE)).isEqualTo(Long.MAX_VALUE);
	}

	@Test
	public void testCallFloat() {
		assertThat(_library.call_float(Float.MIN_VALUE)).isEqualTo(Float.MIN_VALUE);
		assertThat(_library.call_float(0F)).isEqualTo(0F);
		assertThat(_library.call_float(Float.MAX_VALUE)).isEqualTo(Float.MAX_VALUE);
		assertThat(_library.call_float(Float.NaN)).isEqualTo(Float.NaN);

		assertThat(_library.call_Float(Float.MIN_VALUE)).isEqualTo(Float.MIN_VALUE);
		assertThat(_library.call_Float(0F)).isEqualTo(0F);
		assertThat(_library.call_Float(Float.MAX_VALUE)).isEqualTo(Float.MAX_VALUE);
		assertThat(_library.call_Float(Float.NaN)).isEqualTo(Float.NaN);
	}

	@Test
	public void testCallDouble() {
		assertThat(_library.call_double(Double.MIN_VALUE)).isEqualTo(Double.MIN_VALUE);
		assertThat(_library.call_double(0D)).isEqualTo(0D);
		assertThat(_library.call_double(Double.MAX_VALUE)).isEqualTo(Double.MAX_VALUE);
		assertThat(_library.call_double(Double.NaN)).isEqualTo(Double.NaN);

		assertThat(_library.call_Double(Double.MIN_VALUE)).isEqualTo(Double.MIN_VALUE);
		assertThat(_library.call_Double(0D)).isEqualTo(0D);
		assertThat(_library.call_Double(Double.MAX_VALUE)).isEqualTo(Double.MAX_VALUE);
		assertThat(_library.call_Double(Double.NaN)).isEqualTo(Double.NaN);
	}

	@Test
	public void testCallString() {
		assertThat(_library.call_string(null)).isEqualTo(null);
		assertThat(_library.call_string("hello")).isEqualTo("hello");
	}

	@Test
	public void testCallStringArray() {
		assertThat(_library.call_string_array(null)).isNull();

		String[] values = new String[]{"Hello", "World"};
		assertThat(_library.call_string_array(values)).isEqualTo(values);
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

		public byte call_byte_java(byte value) {
			return value;
		}

		public Byte call_Byte_java(Byte value) {
			return value;
		}

		public short call_short_java(short value) {
			return value;
		}

		public Short call_Short_java(Short value) {
			return value;
		}

		public char call_char_java(char value) {
			return value;
		}

		public Character call_Character_java(Character value) {
			return value;
		}

		public int call_int_java(int value) {
			return value;
		}

		public Integer call_Integer_java(Integer value) {
			return value;
		}

		public long call_long_java(long value) {
			return value;
		}

		public Long call_Long_java(Long value) {
			return value;
		}

		public float call_float_java(float value) {
			return value;
		}

		public Float call_Float_java(Float value) {
			return value;
		}

		public double call_double_java(double value) {
			return value;
		}

		public Double call_Double_java(Double value) {
			return value;
		}

		public String call_string_java(String value) {
			return value;
		}

		public String[] call_string_array_java(String[] value) {
			return value;
		}
	}

	interface DefineLibrary extends Library {
		void call_void();
		int call_two(int value0, int value1);

		boolean call_boolean(boolean value);
		Boolean call_Boolean(Boolean value);
		byte call_byte(byte value);
		Byte call_Byte(Byte value);
		short call_short(short value);
		Short call_Short(Short value);
		char call_char(char value);
		Character call_Character(Character value);
		int call_int(int value);
		Integer call_Integer(Integer value);
		long call_long(long value);
		Long call_Long(Long value);
		float call_float(float value);
		Float call_Float(Float value);
		double call_double(double value);
		Double call_Double(Double value);

		String call_string(String value);
		String[] call_string_array(String[] value);
	}
}
