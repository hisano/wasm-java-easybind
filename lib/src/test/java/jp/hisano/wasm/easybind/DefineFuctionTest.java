package jp.hisano.wasm.easybind;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DefineFuctionTest {
	@AfterEach
	public void tearDown() {
		LibraryContext.get().dispose();
	}

	@Test
	public void testCall() {
		LibraryContext context = LibraryContext.get();

		EnvModule envModule = new EnvModule();
		context.defineModule(envModule);

		DefineLibrary defineLibrary = Native.load("define.wasm", DefineLibrary.class);
		defineLibrary.call();

		Assertions.assertThat(envModule._isCalled).isTrue();
	}

	static class EnvModule implements Module {
		boolean _isCalled;

		public void call_java() {
			_isCalled = true;
		}
	}

	public interface DefineLibrary extends Library {
		void call();
	}
}
