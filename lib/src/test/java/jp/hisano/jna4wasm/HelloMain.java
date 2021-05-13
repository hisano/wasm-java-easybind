package jp.hisano.jna4wasm;

public class HelloMain {
	public static void main(String[] args) {
		Hello library = Native.load("hello.wasm", Hello.class);
		library.hello("World");
	}

	public interface Hello extends Library {
		void hello(String name);
	}
}
