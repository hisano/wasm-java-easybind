docker run -it --rm -v %cd%:/src emscripten/emsdk:2.0.20 emcc --no-entry -s EXPORTED_FUNCTIONS="['_hello', '_malloc', '_free']" -s WASM=1 -o hello.wasm hello.c
docker run -it --rm -v %cd%:/src emscripten/emsdk:2.0.20 emcc --no-entry -s EXPORTED_FUNCTIONS="['_add', '_malloc', '_free']" -s WASM=1 -o add.wasm add.c

docker run -it --rm -v %cd%:/src emscripten/emsdk:2.0.20 emcc --no-entry -s ERROR_ON_UNDEFINED_SYMBOLS=0 -s EXPORTED_FUNCTIONS="['_returnFalse', '_dependentReturnFalse', '_malloc', '_free']" -s WASM=1 -o testlib2.wasm testlib2.c
