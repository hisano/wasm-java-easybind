package jp.hisano.jna4wasm.example;

import jp.hisano.jna4wasm.Library;
import jp.hisano.jna4wasm.Native;

public class HelloMain {
	public static void main(String[] args) {
		Hello library = Native.load("hello.wasm", Hello.class);
		library.hello("World");
	}

	public interface Hello extends Library {
		void hello(String name);
	}
}
