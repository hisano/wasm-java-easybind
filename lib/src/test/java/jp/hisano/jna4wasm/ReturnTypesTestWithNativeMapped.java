/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package jp.hisano.jna4wasm;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class ReturnTypesTestWithNativeMapped extends TestCase {

    private static final String UNICODE = "[\u0444]";
    private static final int INT_MAGIC = 0x12345678;
    private static final long LONG_MAGIC = 0x123456789ABCDEF0L;
    private static final double DOUBLE_MAGIC = -118.625d;
    private static final float FLOAT_MAGIC = -118.625f;
    private static final String STRING_MAGIC = "magic";

    public static interface TestLibrary extends Library {

        public static class SimpleStructure extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("value");
            public double value;
            public static int allocations = 0;
            public SimpleStructure() { }
            public SimpleStructure(Pointer p) { super(p); read(); }
            @Override
            protected void allocateMemory(int size) {
                super.allocateMemory(size);
                ++allocations;
            }
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        public static class TestSmallStructure extends Structure {
            public static class ByValue extends TestSmallStructure implements Structure.ByValue { }

            public static final List<String> FIELDS = createFieldsOrder("c1", "c2", "s");
            public byte c1;
            public byte c2;
            public short s;
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        public static class TestStructure extends Structure {
            public static class ByValue extends TestStructure implements Structure.ByValue { }

            public static final List<String> FIELDS = createFieldsOrder("c", "s", "i", "j", "inner");
            public byte c;
            public short s;
            public int i;
            public long j;
            public SimpleStructure inner;
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        public static class CheckFieldAlignment extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("int32Field", "int64Field", "floatField", "doubleField");
            public int int32Field = 1;
            public long int64Field = 2;
            public float floatField = 3f;
            public double doubleField = 4d;
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        class TestObject { }
        Object returnObjectArgument(Object s);
        TestObject returnObjectArgument(TestObject s);
        Class returnClass(JNIEnv env, Object arg);
        boolean returnFalse();
        boolean returnTrue();
        int returnInt32Zero();
        int returnInt32Magic();
        long returnInt64Zero();
        long returnInt64Magic();
        NativeLong returnLongZero();
        NativeLong returnLongMagic();
        float returnFloatZero();
        float returnFloatMagic();
        double returnDoubleZero();
        double returnDoubleMagic();
        String returnStringMagic();
        WString returnWStringMagic();
        SimpleStructure returnStaticTestStructure();
        SimpleStructure returnNullTestStructure();
        TestSmallStructure.ByValue returnSmallStructureByValue();
        TestStructure.ByValue returnStructureByValue();

        Pointer[] returnPointerArgument(Pointer[] arg);
        String[] returnPointerArgument(String[] arg);
        WString[] returnPointerArgument(WString[] arg);
    }

    TestLibrary libSupportingObject;

    @Override
    protected void setUp() {
        libSupportingObject = Native.load("testlib", TestLibrary.class,
                Collections.singletonMap(Library.OPTION_ALLOW_OBJECTS, Boolean.TRUE));
    }

    @Override
    protected void tearDown() {
        LibraryContext.get().dispose();
        libSupportingObject = null;
    }

    public void testReturnObject() throws Exception {
        assertNull("null value not returned", libSupportingObject.returnObjectArgument(null));
        final Object VALUE = new Object() {
            @Override
            public String toString() {
                return getName();
            }
        };
        assertEquals("Wrong object returned", VALUE, libSupportingObject.returnObjectArgument(VALUE));
    }

    public void testReturnClass() throws Exception {
        assertEquals("Wrong class returned", Class.class,
                libSupportingObject.returnClass(JNIEnv.CURRENT, TestLibrary.class));
        assertEquals("Wrong class returned", StringBuilder.class,
                libSupportingObject.returnClass(JNIEnv.CURRENT, new StringBuilder()));
    }

    public interface NativeMappedLibrary extends Library {
        Custom returnInt32Argument(int arg);
        size_t returnInt32Magic();
        size_t returnInt64Magic();
    }
    public static class size_t extends IntegerType {
        private static final long serialVersionUID = 1L;
        public size_t() {
            this(0);
        }
        public size_t(long value) {
            super(Native.SIZE_T_SIZE, true);
            setValue(value);
        }
    }
    public static class Custom implements NativeMapped {
        private int value;
        public Custom() { }
        public Custom(int value) {
            this.value = value;
        }
        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            return new Custom(((Integer)nativeValue).intValue());
        }
        @Override
        public Class<?> nativeType() {
            return Integer.class;
        }
        @Override
        public Object toNative() {
            return Integer.valueOf(value);
        }
        @Override
        public boolean equals(Object o) {
            return o instanceof Custom && ((Custom)o).value == value;
        }
    }

    public static void main(String[] argList) {
        junit.textui.TestRunner.run(ReturnTypesTestWithNativeMapped.class);
    }

}
