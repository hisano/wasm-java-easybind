package jp.hisano.jna4wasm.experiment;

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

public class Hello2 {
    public static void main(String[] args) {
        try {
            byte[] wasm = ByteStreams.toByteArray(Hello2.class.getResourceAsStream("/hello2.wasm"));

            Store store = new Store();
            Engine engine = store.engine();
            Module module = Module.fromBinary(engine, wasm);
            Func helloFunc = WasmFunctions.wrap(store, () -> {
                System.err.println("CB!! Calling back...");
                System.err.println("CB!! > Hello World!");
            });
            Collection<Extern> imports = Arrays.asList(Extern.fromFunc(helloFunc));
            Instance instance = new Instance(store, module, imports);

            WasmFunctions.Consumer0 run = WasmFunctions.consumer(instance.getFunc("run").get());
            run.accept();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
