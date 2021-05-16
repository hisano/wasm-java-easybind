#include <stdio.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

extern void call_java();
extern int call_int_java(int value);

void call() {
	call_java();
}

int call_int(int value) {
	return call_int_java(value);
}

#ifdef __cplusplus
}
#endif
