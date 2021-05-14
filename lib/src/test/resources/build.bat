docker run -it --rm -v %cd%:/src emscripten/emsdk:2.0.20 emcc --no-entry -s EXPORTED_FUNCTIONS="['_hello', '_malloc', '_free']" -s WASM=1 -o hello.wasm hello.c
docker run -it --rm -v %cd%:/src emscripten/emsdk:2.0.20 emcc --no-entry -s EXPORTED_FUNCTIONS="['_add', '_malloc', '_free']" -s WASM=1 -o add.wasm add.c

docker run -it --rm -v %cd%:/src emscripten/emsdk:2.0.20 emcc --no-entry -s EXPORTED_FUNCTIONS="['_returnInt32Argument', '_returnStringArgument', '_returnBooleanArgument', '_returnInt8Argument', '_returnWideCharArgument', '_returnInt16Argument', '_returnInt32Argument', '_returnInt64Argument', '_returnLongArgument', '_returnPointerArgument', '_returnStringArgument', '_returnWStringArgument', '_checkInt64ArgumentAlignment', '_checkDoubleArgumentAlignment', '_testStructurePointerArgument', '_testStructurePointerArgument', '_testStructureByValueArgument', '_testStructureArrayInitialization', '_modifyStructureArray', '_testStructureByReferenceArrayInitialization', '_modifyStructureByReferenceArray', '_fillInt8Buffer', '_fillInt16Buffer', '_fillInt32Buffer', '_fillInt64Buffer', '_returnStringArrayElement', '_returnWideStringArrayElement', '_returnPointerArrayElement', '_returnRotatedArgumentCount', '_setCallbackInStruct', '_returnStringFromVariableSizedStructure', '_fillFloatBuffer', '_fillDoubleBuffer', '_incrementInt8ByReference', '_incrementInt16ByReference', '_incrementInt32ByReference', '_incrementNativeLongByReference', '_incrementInt64ByReference', '_complementFloatByReference', '_complementDoubleByReference', '_setPointerByReferenceNull', '_returnFloatArgument', '_returnDoubleArgument', '_stringifyMixedUnion1', '_returnLongZero', '_returnLongMagic', '_returnStringMagic', '_returnNullTestStructure', '_returnInt32Zero', '_returnInt32Magic', '_returnWStringMagic', '_returnDoubleZero', '_returnDoubleMagic', '_returnFloatZero', '_returnFloatMagic', '_returnStaticTestStructure', '_returnInt64Zero', '_returnInt64Magic', '_returnFalse', '_returnTrue', '_returnSmallStructureByValue', '_returnStructureByValue', '_testStructureByValueArgument16', '_testStructureByValueArgument32', '_testStructureByValueArgument64', '_testStructureByValueArgument128', '_testStructureByValueArgument8', '_malloc', '_free']" -s WASM=1 -o testlib.wasm testlib.c
docker run -it --rm -v %cd%:/src emscripten/emsdk:2.0.20 emcc --no-entry -s ERROR_ON_UNDEFINED_SYMBOLS=0 -s EXPORTED_FUNCTIONS="['_returnFalse', '_dependentReturnFalse', '_malloc', '_free']" -s WASM=1 -o testlib2.wasm testlib2.c

docker run -it --rm -v %cd%:/src emscripten/emsdk:2.0.20 emcc --no-entry -s EXPORTED_FUNCTIONS="['_dummy', '_malloc', '_free']" -s WASM=1 -o dummy.wasm dummy.c
