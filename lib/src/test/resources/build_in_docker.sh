#!/bin/bash

function compile {
  file_base_name=$1
  exported_fuctions=$2
  
  if [ ${file_base_name}.c -nt ${file_base_name}.wasm ]
  then
    echo "Compile ${file_base_name}.c to ${file_base_name}.wasm"
    emcc --no-entry -s EXPORTED_FUNCTIONS="${exported_fuctions}" -s ERROR_ON_UNDEFINED_SYMBOLS=0 -s WASM=1 -o ${file_base_name}.wasm ${file_base_name}.c
  fi
}

function compile_all_files {
  library_required_functions="'_malloc', '_free'"

  compile add "['_add', ${library_required_functions}]"
  compile dummy "['_dummy', ${library_required_functions}]"
  compile hello "['_hello', ${library_required_functions}]"
  compile testlib "['_returnInt32Argument', '_returnStringArgument', '_returnBooleanArgument', '_returnInt8Argument', '_returnWideCharArgument', '_returnInt16Argument', '_returnInt32Argument', '_returnInt64Argument', '_returnLongArgument', '_returnPointerArgument', '_returnStringArgument', '_returnWStringArgument', '_checkInt64ArgumentAlignment', '_checkDoubleArgumentAlignment', '_testStructurePointerArgument', '_testStructurePointerArgument', '_testStructureByValueArgument', '_testStructureArrayInitialization', '_modifyStructureArray', '_testStructureByReferenceArrayInitialization', '_modifyStructureByReferenceArray', '_fillInt8Buffer', '_fillInt16Buffer', '_fillInt32Buffer', '_fillInt64Buffer', '_returnStringArrayElement', '_returnWideStringArrayElement', '_returnPointerArrayElement', '_returnRotatedArgumentCount', '_setCallbackInStruct', '_returnStringFromVariableSizedStructure', '_fillFloatBuffer', '_fillDoubleBuffer', '_incrementInt8ByReference', '_incrementInt16ByReference', '_incrementInt32ByReference', '_incrementNativeLongByReference', '_incrementInt64ByReference', '_complementFloatByReference', '_complementDoubleByReference', '_setPointerByReferenceNull', '_returnFloatArgument', '_returnDoubleArgument', '_stringifyMixedUnion1', '_returnLongZero', '_returnLongMagic', '_returnStringMagic', '_returnNullTestStructure', '_returnInt32Zero', '_returnInt32Magic', '_returnWStringMagic', '_returnDoubleZero', '_returnDoubleMagic', '_returnFloatZero', '_returnFloatMagic', '_returnStaticTestStructure', '_returnInt64Zero', '_returnInt64Magic', '_returnFalse', '_returnTrue', '_returnSmallStructureByValue', '_returnStructureByValue', '_testStructureByValueArgument16', '_testStructureByValueArgument32', '_testStructureByValueArgument64', '_testStructureByValueArgument128', '_testStructureByValueArgument8', '_getStructureSize', '_testStructureAlignment', ${library_required_functions}]"
  compile testlib2 "['_returnFalse', '_dependentReturnFalse', ${library_required_functions}]"
  compile define "['_call_void', '_call_two', '_call_boolean', '_call_Boolean', '_call_byte', '_call_Byte', '_call_short', '_call_Short', '_call_char', '_call_Character', '_call_int', '_call_Integer', '_call_long', '_call_Long', '_call_float', '_call_Float', '_call_double', '_call_Double', '_call_string', '_call_string_array', ${library_required_functions}]"
}

cd $(dirname $0)
compile_all_files
