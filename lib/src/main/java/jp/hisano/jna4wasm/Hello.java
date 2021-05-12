package jp.hisano.jna4wasm;

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

public class Hello {
    public static void main(String[] args) {
        try {
            byte[] wasm = ByteStreams.toByteArray(Hello.class.getResourceAsStream("/hello.wasm"));
            // Configure the initial compilation environment, creating the global
            // `Store` structure. Note that you can also tweak configuration settings
            // with a `Config` and an `Engine` if desired.
            System.err.println("Initializing...");

            Store store = new Store();
            Linker linker = new Linker(store);

            Wasi wasi = new Wasi(store, new WasiConfig(new String[0], new WasiConfig.PreopenDir[0]));
            Module module = Module.fromBinary(store.engine(), wasm);

            wasi.addToLinker(linker);
            linker.module("", module);

            Memory memory = linker.getOneByName("", "memory").memory();

            byte[] subject = "Wasmer".getBytes(StandardCharsets.UTF_8);

            // Allocate memory for the subject, and get a pointer to it.
            Func allocate = linker.getOneByName("", "malloc").func();
            int input_pointer = allocate.call(Val.fromI32(subject.length))[0].i32();

            // Write the subject into the memory.
            {
                ByteBuffer memoryBuffer = memory.buffer();
                memoryBuffer.position(input_pointer);
                memoryBuffer.put(subject);
            }

            // Run the `greet` function. Give the pointer to the subject.
            int output_pointer = linker.getOneByName("", "hello").func().call(Val.fromI32(input_pointer))[0].i32();

            // Read the result of the `greet` function.
            String result;

            {
                StringBuilder output = new StringBuilder();
                ByteBuffer memoryBuffer = memory.buffer();

                for (Integer i = output_pointer, max = memoryBuffer.limit(); i < max; ++i) {
                    byte[] b = new byte[1];
                    memoryBuffer.position(i);
                    memoryBuffer.get(b);

                    if (b[0] == 0) {
                        break;
                    }

                    output.appendCodePoint(b[0]);
                }

                result = output.toString();
            }

            System.out.println(result);
            assert result.equals("Hello, Wasmer!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
