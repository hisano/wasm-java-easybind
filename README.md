wasm-java-easybind
==================

wasm-java-easybind enables developers write Java programs with WebAssembly libraries easily. This library has APIs like JNA (Java Native Access).

Features
========

* Automatic mapping from Java to native functions, with simple mappings for all primitive data types
* Automatic conversion between C and Java strings, with customizable encoding/decoding
* Structure and Union arguments/return values, by reference and by value
* By-reference (pointer-to-type) arguments
* Java array and NIO Buffer arguments (primitive types and pointers) as pointer-to-buffer
* Nested structures and arrays
* Wide (wchar_t-based) strings
* Customizable marshalling/unmarshalling (argument and return value conversions)
* Customizable mapping from Java method to native function name, and customizable invocation to simulate C preprocessor function macros
* Support for automatic Windows ASCII/UNICODE function mappings
* Type-safety for native pointers

Example
=======

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
```

License
=======

This library is licensed under the Apache Software License.
