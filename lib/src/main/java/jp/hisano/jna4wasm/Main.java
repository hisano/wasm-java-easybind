package jp.hisano.jna4wasm;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.wasmer.Instance;
import org.wasmer.Memory;

import com.google.common.io.ByteStreams;

public class Main {
	public static void main(String[] args) throws Exception {
		// Instantiates the module.
		byte[] bytes = ByteStreams.toByteArray(Main.class.getResourceAsStream("/greet.wasm"));
		Instance instance = new Instance(bytes);
		Memory memory = instance.exports.getMemory("memory");

		// Set the subject to greet.
		byte[] subject = "Wasmer".getBytes(StandardCharsets.UTF_8);

		// Allocate memory for the subject, and get a pointer to it.
		Integer input_pointer = (Integer) instance.exports.getFunction("allocate").apply(subject.length)[0];

		// Write the subject into the memory.
		{
			ByteBuffer memoryBuffer = memory.buffer();
			memoryBuffer.position(input_pointer);
			memoryBuffer.put(subject);
		}

		// Run the `greet` function. Give the pointer to the subject.
		Integer output_pointer = (Integer) instance.exports.getFunction("greet").apply(input_pointer)[0];

		// Read the result of the `greet` function.
		String result;

		{
			StringBuilder output = new StringBuilder();
			ByteBuffer memoryBuffer = memory.buffer();

			for (Integer i = output_pointer, max = memoryBuffer.limit(); i < max; ++i) {
				byte[] b = new byte[1];
				memoryBuffer.position(i);
				memoryBuffer.get(b);

				if (b[0] == 0) {
					break;
				}

				output.appendCodePoint(b[0]);
			}

			result = output.toString();
		}

		System.out.println(result);
		assert result.equals("Hello, Wasmer!");

		instance.close();
	}
}
