#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

extern void call_java();

extern bool call_boolean_java(bool value);
extern int call_int_java(int value);

extern char * call_string_java(char* value);

void call() {
	call_java();
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
