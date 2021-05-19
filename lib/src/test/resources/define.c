#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

extern void call_void_java();
extern int call_two_java(int value0, int value1);

extern bool call_boolean_java(bool value);
extern bool call_Boolean_java(bool value);
extern char call_byte_java(char value);
extern char call_Byte_java(char value);
extern short call_short_java(short value);
extern short call_Short_java(short value);
extern short call_char_java(short value);
extern short call_Character_java(short value);
extern int call_int_java(int value);
extern int call_Integer_java(int value);
extern long long call_long_java(long long value);
extern long long call_Long_java(long long value);
extern float call_float_java(float value);
extern float call_Float_java(float value);
extern double call_double_java(double value);
extern double call_Double_java(double value);

extern char* call_string_java(char* value);
extern char** call_string_array_java(char** value);

void call_void() {
	call_void_java();
}

int call_two(int value0, int value1) {
	return call_two_java(value0, value1);
}

bool call_boolean(bool value) {
	return call_boolean_java(value);
}

bool call_Boolean(bool value) {
	return call_Boolean_java(value);
}

char call_byte(char value) {
	return call_byte_java(value);
}

char call_Byte(char value) {
	return call_Byte_java(value);
}

short call_short(short value) {
	return call_short_java(value);
}

short call_Short(short value) {
	return call_Short_java(value);
}

short call_char(short value) {
	return call_char_java(value);
}

short call_Character(short value) {
	return call_Character_java(value);
}

int call_int(int value) {
	return call_int_java(value);
}

int call_Integer(int value) {
	return call_Integer_java(value);
}

long long call_long(long long value) {
	return call_long_java(value);
}

long long call_Long(long long value) {
	return call_Long_java(value);
}

float call_float(float value) {
	return call_float_java(value);
}

float call_Float(float value) {
	return call_Float_java(value);
}

double call_double(double value) {
	return call_double_java(value);
}

double call_Double(double value) {
	return call_Double_java(value);
}

char* call_string(char* value) {
	return call_string_java(value);
}

// The last array element is null. This null is appended in the library.
char** call_string_array(char** value) {
	return call_string_array_java(value);
}

#ifdef __cplusplus
}
#endif
