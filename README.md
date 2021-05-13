Java Native Access for WebAssembly (jna4wasm)
=============================================

jna4wasm enables developers write Java programs with WebAssembly libraries easily. This library has APIs like JNA.
 
How to use
==========

* Write C code and save it as 'hello.c'

```c
#include <stdio.h>
#include <stdlib.h>

void hello(char *name) {
 	printf("Hello, %s!\n", name);
}
```

* Compile 'hello.c' to 'hello.wasm' with Emscripten on your shell

```shell
docker run -it --rm -v $(pwd):/src emscripten/emsdk:2.0.20 emcc --no-entry -s EXPORTED_FUNCTIONS="['_hello', '_malloc', '_free']" -s WASM=1 -o hello.wasm hello.c
```

* Write Java code and run it

```java
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
```

License
=======

This library is licensed under the Apache Software License.
