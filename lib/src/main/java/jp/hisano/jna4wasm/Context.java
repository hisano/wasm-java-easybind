package jp.hisano.jna4wasm;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

import io.github.kawamuray.wasmtime.Disposable;
import io.github.kawamuray.wasmtime.Func;
import io.github.kawamuray.wasmtime.Linker;
import io.github.kawamuray.wasmtime.Memory;
import io.github.kawamuray.wasmtime.Module;
import io.github.kawamuray.wasmtime.Store;
import io.github.kawamuray.wasmtime.Val;
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

	private Func _malloc;
	private Func _free;

	private Context() {
		_store = new Store();
		_linker = new Linker(_store);
		Wasi wasi = new Wasi(_store, new WasiConfig(new String[0], new WasiConfig.PreopenDir[0]));
		wasi.addToLinker(_linker);
	}

	public void load(byte[] wasmBytes) {
		_linker.module("", Module.fromBinary(_store.engine(), wasmBytes));
		_memory = _linker.getOneByName("", "memory").memory();
		_malloc = _linker.getOneByName("", "malloc").func();
		_free = _linker.getOneByName("", "free").func();
	}

	public Object invokeFunction(String fp, Object[] args) {
		Val[] results = _linker.getOneByName("", fp).func().call(convertToVal(args));

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

	private Val[] convertToVal(Object[] args) {
		return Stream.of(args).map(argument -> {
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

	public int malloc(int size) {
		return _malloc.call(Val.fromI32(size))[0].i32();
	}

	public void free(int ptr) {
		_free.call(Val.fromI32(ptr));
	}

	public void write(int address, byte[] array) {
		ByteBuffer buffer = _memory.buffer();
		buffer.position(address);
		buffer.put(array);
	}

	public void dispose() {
	}
}
