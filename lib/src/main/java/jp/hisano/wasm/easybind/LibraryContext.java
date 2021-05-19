package jp.hisano.wasm.easybind;

import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import io.github.kawamuray.wasmtime.Disposable;
import io.github.kawamuray.wasmtime.Extern;
import io.github.kawamuray.wasmtime.Func;
import io.github.kawamuray.wasmtime.FuncType;
import io.github.kawamuray.wasmtime.Linker;
import io.github.kawamuray.wasmtime.Memory;
import io.github.kawamuray.wasmtime.Store;
import io.github.kawamuray.wasmtime.Val;
import io.github.kawamuray.wasmtime.WasmtimeException;
import io.github.kawamuray.wasmtime.wasi.Wasi;
import io.github.kawamuray.wasmtime.wasi.WasiConfig;

public final class LibraryContext implements Disposable {
	private static final LibraryContext INSTANCE = new LibraryContext();

	public static LibraryContext get() {
		return INSTANCE;
	}

	private static final Map<Class<?>, ValueConverter> VALUE_CONVERTERS = new ConcurrentHashMap<>();
	static {
		addConverters();
	}

	private static void addConverters() {
		addConverter(new Class<?>[]{ boolean.class, Boolean.class, int.class, Integer.class }, Val.Type.I32, jnaValue -> Val.fromI32((Integer) jnaValue), wasmValue -> wasmValue.i32());
		addConverter(new Class<?>[]{ byte.class, Byte.class }, Val.Type.I32, jnaValue -> Val.fromI32((Byte) jnaValue), wasmValue -> (byte)wasmValue.i32());
		addConverter(new Class<?>[]{ short.class, Short.class }, Val.Type.I32, jnaValue -> Val.fromI32((Short) jnaValue), wasmValue -> (short)wasmValue.i32());
		addConverter(new Class<?>[]{ char.class, Character.class }, Val.Type.I32, jnaValue -> Val.fromI32((Character) jnaValue), wasmValue -> (char)wasmValue.i32());
		addConverter(new Class<?>[]{ long.class, Long.class }, Val.Type.I64, jnaValue -> Val.fromI64((Long) jnaValue), wasmValue -> wasmValue.i64());

		addConverter(new Class<?>[]{ float.class, Float.class }, Val.Type.F32, jnaValue -> Val.fromF32((Float) jnaValue), wasmValue -> wasmValue.f32());
		addConverter(new Class<?>[]{ double.class, Double.class }, Val.Type.F64, jnaValue -> Val.fromF64((Double) jnaValue), wasmValue -> wasmValue.f64());

		addConverter(String.class, Val.Type.I32, jnaValue -> Val.fromI32((int) ((jp.hisano.wasm.easybind.Memory) jnaValue).peer), wasmValue -> new Pointer(wasmValue.i32()));
		addConverter(String[].class, Val.Type.I32, jnaValue -> {
			if (jnaValue == null) {
				return Val.fromI32(0);
			}
			return Val.fromI32((int) ((jp.hisano.wasm.easybind.Memory) jnaValue).peer);
		}
		, wasmValue -> new Pointer(wasmValue.i32()));
	}

	private static void addConverter(Class<?> javaType, Val.Type wasmType, ToWasmValueConverter toWasmValueConverter, ToJnaValueConverter toJnaValueConverter) {
		addConverter(new Class<?>[] { javaType }, wasmType, toWasmValueConverter, toJnaValueConverter);
	}

	private static void addConverter(Class<?>[] javaTypes, Val.Type wasmType, ToWasmValueConverter toWasmValueConverter, ToJnaValueConverter toJnaValueConverter) {
		ValueConverter converter = new ValueConverter() {
			@Override
			public Val.Type toWasmTypes(Class<?> javaType) {
				return wasmType;
			}

			@Override
			public Object toJnaValue(Val wasmValue, Class javaType) {
				return toJnaValueConverter.toJnaValue(wasmValue);
			}

			@Override
			public Val toWasmValue(Object jnaValue, Class<?> javaType) {
				return toWasmValueConverter.toWasmValue(jnaValue);
			}
		};
		Stream.of(javaTypes).forEach(javaType -> VALUE_CONVERTERS.put(javaType, converter));
	}

	private Store _store;
	private Linker _linker;

	private Memory _memory;
	private ByteBuffer _memoryBuffer;

	private final List<Disposable> _resources = new CopyOnWriteArrayList<>();

	private final Map<String, Func> _functions = new ConcurrentHashMap<>();

	private LibraryContext() {
	}

	synchronized Store getStore() {
		lateInit();

		return _store;
	}

	public synchronized void defineModule(Module module) {
		defineModule("env", module);
	}

	public synchronized void defineModule(String moduleName,Module module) {
		lateInit();

		Stream.of(module.getClass().getDeclaredMethods()).forEach(method -> {
			if (!Modifier.isPublic(method.getModifiers())) {
				return;
			}

			String functionName = method.getName();

			CallbackProxy callbackProxy = CallbackReference.createCallbackProxy(module, method, null);

			FuncType functionType = new FuncType(toWasmTypes(method.getParameterTypes()), toWasmTypes(new Class[]{ method.getReturnType() }));
			Func.Handler functionHandler = (caller, wasmArguments, wasmResults) -> {
				Object[] jnaArguments = toJnaValues(wasmArguments, method.getParameterTypes());
				Object jnaResult = callbackProxy.callback(jnaArguments);
				if (method.getReturnType() != void.class) {
					wasmResults[0] = toWasmValue(jnaResult, method.getReturnType());
				}
				return Optional.empty();
			};
			_linker.define(moduleName, functionName, Extern.fromFunc(new Func(getStore(), functionType, functionHandler)));
		});
	}

	private Val toWasmValue(Object jnaValue, Class<?> javaType) {
		return getConverter(javaType, "while returning wasm value from jna value").toWasmValue(jnaValue, javaType);
	}

	private Object[] toJnaValues(Val[] wasmValues, Class[] javaTypes) {
		Object[] jnaValues = new Object[wasmValues.length];
		for (int i = 0; i < wasmValues.length; i++) {
			jnaValues[i] = getConverter(javaTypes[i], "while calling with wasm value").toJnaValue(wasmValues[i], javaTypes[i]);
		}
		return jnaValues;
	}

	private Val.Type[] toWasmTypes(Class<?>[] javaTypes) {
		if (javaTypes.length == 1 && javaTypes[0] == void.class) {
			return new Val.Type[0];
		}
		return Stream.of(javaTypes).map(type -> getConverter(type, "while defining function").toWasmTypes(type)).toArray(Val.Type[]::new);
	}

	private ValueConverter getConverter(Class<?> javaType, String errorMessageAppendix) {
		ValueConverter converter = VALUE_CONVERTERS.get(javaType);

		if (converter == null) {
			throw new IllegalArgumentException("unsupported type " + errorMessageAppendix + ": type = " + javaType.getSimpleName());
		}

		return converter;
	}

	private void lateInit() {
		if (_store != null) {
			return;
		}

		_store = addResource(new Store());
		_linker = addResource(new Linker(_store));

		Wasi wasi = addResource(new Wasi(_store, new WasiConfig(new String[0], new WasiConfig.PreopenDir[0])));
		wasi.addToLinker(_linker);
	}

	synchronized void loadBinary(byte[] wasmBytes) {
		lateInit();

		_linker.module("", addResource(io.github.kawamuray.wasmtime.Module.fromBinary(_store.engine(), wasmBytes)));
		_memory = addResource(_linker.getOneByName("", "memory").memory());
		_memoryBuffer = _memory.buffer();
	}

	private <T extends Disposable> T addResource(T resource) {
		_resources.add(0, resource);
		return resource;
	}

	public synchronized void dispose() {
		if (_store == null) {
			return;
		}

		Native.dispose();

		_resources.forEach(Disposable::close);
		_resources.clear();

		_functions.clear();

		_memoryBuffer = null;
		_memory = null;

		_linker = null;
		_store = null;
	}

	private void requireDisposed() {
		if (_store != null) {
			throw new IllegalStateException();
		}
	}

	private void requireNotDisposed() {
		if (_store == null) {
			throw new IllegalStateException();
		}
	}

	synchronized Object invokeFunction(String functionName, Object... arguments) {
		requireNotDisposed();

		Val[] results;
		try {
			Func func = _functions.get(functionName);
			if (func == null) {
				func = addResource(_linker.getOneByName("", functionName).func());
				_functions.put(functionName, func);
			}

			results = func.call(convertToVal(arguments));
		} catch (WasmtimeException e) {
			throw new UnsatisfiedLinkError("no '_" + functionName + "' function or calling failure: message = " + e.getMessage());
		}

		if (results.length == 0) {
			return null;
		}

		return convertFromVal(results[0]);
	}

	private Object convertFromVal(Val result) {
		switch (result.getType()) {
			case I32:
				return result.i32();
			case I64:
				return result.i64();
			case F32:
				return result.f32();
			case F64:
				return result.f64();
			default:
				throw new IllegalArgumentException();
		}
	}

	private Val[] convertToVal(Object... arguments) {
		return Stream.of(arguments).map(argument -> {
			if (argument instanceof Boolean) {
				return Val.fromI32(((Boolean)argument)? 1: 0);
			} else if (argument instanceof Byte) {
				return Val.fromI32(((Byte)argument).byteValue());
			} else if (argument instanceof Character) {
				return Val.fromI32((Character)argument);
			} else if (argument instanceof Short) {
				return Val.fromI32(((Short)argument).shortValue());
			} else if (argument instanceof Integer) {
				return Val.fromI32(((Integer) argument).intValue());
			} else if (argument instanceof Long) {
				return Val.fromI64(((Long) argument).longValue());
			} else if (argument instanceof Float) {
				return Val.fromF32(((Float) argument).floatValue());
			} else if (argument instanceof Double) {
				return Val.fromF64(((Double) argument).doubleValue());
			} else if (argument instanceof Pointer) {
				return Val.fromI32((int) ((Pointer) argument).peer);
			} else if (argument == null){
				return Val.fromI32(0);
			} else {
				throw new IllegalArgumentException(argument.getClass() + " is not supported");
			}
		}).toArray(Val[]::new);
	}

	synchronized ByteBuffer getMemoryBuffer() {
		requireNotDisposed();

		return _memoryBuffer;
	}

	private interface ToJnaValueConverter {
		Object toJnaValue(Val wasmValue);
	}

	private interface ToWasmValueConverter {
		Val toWasmValue(Object jnaValue);
	}

	private interface ValueConverter {
		Val.Type toWasmTypes(Class<?> javaType);
		Object toJnaValue(Val wasmValue, Class<?> javaType);
		Val toWasmValue(Object jnaValue, Class<?> javaType);
	}

	interface ValueChecker<T> {
		boolean isValidValue(T value);
	}
}
