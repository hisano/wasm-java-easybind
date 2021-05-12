package jp.hisano.jna4wasm.experiment;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import com.google.common.io.ByteStreams;

import io.github.kawamuray.wasmtime.Engine;
import io.github.kawamuray.wasmtime.Func;
import io.github.kawamuray.wasmtime.Instance;
import io.github.kawamuray.wasmtime.Memory;
import io.github.kawamuray.wasmtime.Module;
import io.github.kawamuray.wasmtime.Store;
import io.github.kawamuray.wasmtime.Val;

public class Greet {
    public static void main(String[] args) {
        try {
            byte[] wasm = ByteStreams.toByteArray(Greet.class.getResourceAsStream("/greet.wasm"));

            Store store = new Store();
            Engine engine = store.engine();
            Module module = Module.fromBinary(engine, wasm);
            Instance instance = new Instance(store, module, Collections.emptyList());

            Memory memory = instance.getMemory("memory").get();

            byte[] subject = "Wasmer".getBytes(StandardCharsets.UTF_8);

            // Allocate memory for the subject, and get a pointer to it.
            Func allocate = instance.getFunc("allocate").get();
            int input_pointer = allocate.call(Val.fromI32(subject.length))[0].i32();

            // Write the subject into the memory.
            {
                ByteBuffer memoryBuffer = memory.buffer();
                memoryBuffer.position(input_pointer);
                memoryBuffer.put(subject);
            }

            // Run the `greet` function. Give the pointer to the subject.
            int output_pointer = instance.getFunc("greet").get().call(Val.fromI32(input_pointer))[0].i32();

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
