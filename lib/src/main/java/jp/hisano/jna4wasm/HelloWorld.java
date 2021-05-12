package jp.hisano.jna4wasm;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.io.ByteStreams;

import io.github.kawamuray.wasmtime.Engine;
import io.github.kawamuray.wasmtime.Extern;
import io.github.kawamuray.wasmtime.Func;
import io.github.kawamuray.wasmtime.Instance;
import io.github.kawamuray.wasmtime.Module;
import io.github.kawamuray.wasmtime.Store;
import io.github.kawamuray.wasmtime.WasmFunctions;

public class HelloWorld {
    public static void main(String[] args) {
        try {
            byte[] wasm = ByteStreams.toByteArray(HelloWorld.class.getResourceAsStream("/hello2.wasm"));
            // Configure the initial compilation environment, creating the global
            // `Store` structure. Note that you can also tweak configuration settings
            // with a `Config` and an `Engine` if desired.
            System.err.println("Initializing...");
            try (Store store = new Store()) {
                // Compile the wasm binary into an in-memory instance of a `Module`.
                System.err.println("Compiling module...");
                try (Engine engine = store.engine();
                     Module module = Module.fromBinary(engine, wasm)) {
                    // Here we handle the imports of the module, which in this case is our
                    // `HelloCallback` type and its associated implementation of `Callback.
                    System.err.println("Creating callback...");
                    try (Func helloFunc = WasmFunctions.wrap(store, () -> {
                        System.err.println("CB!! Calling back...");
                        System.err.println("CB!! > Hello World!");
                    })) {
                        // Once we've got that all set up we can then move to the instantiation
                        // phase, pairing together a compiled module as well as a set of imports.
                        // Note that this is where the wasm `start` function, if any, would run.
                        System.err.println("Instantiating module...");
                        Collection<Extern> imports = Arrays.asList(Extern.fromFunc(helloFunc));
                        try (Instance instance = new Instance(store, module, imports)) {
                            // Next we poke around a bit to extract the `run` function from the module.
                            System.err.println("Extracting export...");
                            try (Func f = instance.getFunc("run").get()) {
                                WasmFunctions.Consumer0 fn = WasmFunctions.consumer(f);

                                // And last but not least we can call it!
                                System.err.println("Calling export...");
                                fn.accept();

                                System.err.println("Done.");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
