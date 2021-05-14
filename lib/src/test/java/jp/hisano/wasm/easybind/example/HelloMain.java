package jp.hisano.wasm.easybind.example;

import jp.hisano.wasm.easybind.Library;
import jp.hisano.wasm.easybind.Native;

public class HelloMain {
	public static void main(String[] args) {
		Hello library = Native.load("hello.wasm", Hello.class);
		library.hello("World");
	}

	public interface Hello extends Library {
		void hello(String name);
	}
}
