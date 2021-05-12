docker run -it --rm -v %cd%:/src emscripten/emsdk emcc --no-entry -s EXPORTED_FUNCTIONS="['_hello', '_malloc', '_free']" -s WASM=1 -o hello.wasm hello.c
