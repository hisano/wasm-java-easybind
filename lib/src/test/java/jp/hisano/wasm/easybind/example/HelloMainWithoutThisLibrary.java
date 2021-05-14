package jp.hisano.wasm.easybind.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteStreams;

import io.github.kawamuray.wasmtime.Func;
import io.github.kawamuray.wasmtime.Linker;
import io.github.kawamuray.wasmtime.Memory;
import io.github.kawamuray.wasmtime.Module;
import io.github.kawamuray.wasmtime.Store;
import io.github.kawamuray.wasmtime.Val;
import io.github.kawamuray.wasmtime.wasi.Wasi;
import io.github.kawamuray.wasmtime.wasi.WasiConfig;

public class HelloMainWithoutThisLibrary {
	public static void main(String[] args) throws IOException {
		byte[] wasm = ByteStreams.toByteArray(HelloMainWithoutThisLibrary.class.getResourceAsStream("/hello.wasm"));
		try (Store store = new Store(); 
				Linker linker = new Linker(store);
				Wasi wasi = new Wasi(store, new WasiConfig(new String[0], new WasiConfig.PreopenDir[0]))) {
			wasi.addToLinker(linker);
			try (Module module = Module.fromBinary(store.engine(), wasm)) {
				linker.module("", module);
				try (Memory memory = linker.getOneByName("", "memory").memory()) {
					byte[] world = "World".getBytes(StandardCharsets.UTF_8);

					int inputPointer;
					try (Func malloc = linker.getOneByName("", "malloc").func()) {
						inputPointer = malloc.call(Val.fromI32(world.length))[0].i32();
					}

					ByteBuffer memoryBuffer = memory.buffer();
					memoryBuffer.position(inputPointer);
					memoryBuffer.put(world);

					try (Func hello = linker.getOneByName("", "hello").func()) {
						hello.call(Val.fromI32(inputPointer));
					}
				}
			}
		}
	}
}
