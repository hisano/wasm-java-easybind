#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

extern void call_void_java();
extern int call_two_java(int value0, int value1);

extern bool call_boolean_java(bool value);
extern int call_int_java(int value);

extern char * call_string_java(char* value);

void call_void() {
	call_void_java();
}

int call_two(int value0, int value1) {
	return call_two_java(value0, value1);
}

bool call_boolean(bool value) {
	return call_boolean_java(value);
}

int call_int(int value) {
	return call_int_java(value);
}

char * call_string(char * value) {
	return call_string_java(value);
}

#ifdef __cplusplus
}
#endif
