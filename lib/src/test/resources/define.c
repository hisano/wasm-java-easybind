#include <stdio.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

extern void call_java();

void call() {
	call_java();
}

#ifdef __cplusplus
}
#endif
