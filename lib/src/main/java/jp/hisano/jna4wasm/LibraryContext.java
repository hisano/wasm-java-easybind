package jp.hisano.jna4wasm;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import io.github.kawamuray.wasmtime.Disposable;
import io.github.kawamuray.wasmtime.Func;
import io.github.kawamuray.wasmtime.Linker;
import io.github.kawamuray.wasmtime.Memory;
import io.github.kawamuray.wasmtime.Module;
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

	private Store _store;
	private Linker _linker;

	private Memory _memory;
	private ByteBuffer _memoryBuffer;

	private final List<Disposable> _resources = new CopyOnWriteArrayList<>();

	private final Map<String, Func> _functions = new ConcurrentHashMap<>();

	private LibraryContext() {
	}

	synchronized void loadBinary(byte[] wasmBytes) {
		requireDisposed();

		_store = addResource(new Store());
		_linker = addResource(new Linker(_store));

		Wasi wasi = addResource(new Wasi(_store, new WasiConfig(new String[0], new WasiConfig.PreopenDir[0])));
		wasi.addToLinker(_linker);

		_linker.module("", addResource(Module.fromBinary(_store.engine(), wasmBytes)));
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

	synchronized ByteBuffer getMemoryBuffer() {
		requireNotDisposed();

		return _memoryBuffer;
	}
}
