package jp.hisano.jna4wasm;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

import io.github.kawamuray.wasmtime.Disposable;
import io.github.kawamuray.wasmtime.Linker;
import io.github.kawamuray.wasmtime.Memory;
import io.github.kawamuray.wasmtime.Module;
import io.github.kawamuray.wasmtime.Store;
import io.github.kawamuray.wasmtime.Val;
import io.github.kawamuray.wasmtime.WasmtimeException;
import io.github.kawamuray.wasmtime.wasi.Wasi;
import io.github.kawamuray.wasmtime.wasi.WasiConfig;

public final class Context implements Disposable {
	private static final Context INSTANCE = new Context();

	public static Context get() {
		return INSTANCE;
	}

	private final Store _store;
	private final Linker _linker;

	private Memory _memory;
	private ByteBuffer _memoryBuffer;

	private Context() {
		_store = new Store();
		_linker = new Linker(_store);
		Wasi wasi = new Wasi(_store, new WasiConfig(new String[0], new WasiConfig.PreopenDir[0]));
		wasi.addToLinker(_linker);
	}

	void loadBinary(byte[] wasmBytes) {
		_linker.module("", Module.fromBinary(_store.engine(), wasmBytes));
		_memory = _linker.getOneByName("", "memory").memory();
		_memoryBuffer = _memory.buffer();
	}

	Object invokeFunction(String functionName, Object... arguments) {
		Val[] results;
		try {
			results = _linker.getOneByName("", functionName).func().call(convertToVal(arguments));
		} catch (WasmtimeException e) {
			throw new UnsatisfiedLinkError();
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
			} else if (argument instanceof Short) {
				return Val.fromI32(((Short)argument).shortValue());
			} else if (argument instanceof Integer) {
				return Val.fromI32(((Integer) argument).intValue());
			} else if (argument instanceof Pointer) {
				return Val.fromI32((int) ((Pointer)argument).peer);
			} else {
				throw new IllegalArgumentException();
			}
		}).toArray(Val[]::new);
	}

	ByteBuffer getMemoryBuffer() {
		return _memoryBuffer;
	}

	public void dispose() {
	}
}
