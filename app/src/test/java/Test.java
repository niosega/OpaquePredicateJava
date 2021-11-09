import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import OpaquePredicateJava.OpaquePredicateObfuscator;

public class Test {
	@BeforeClass
	public static void setUp() {
		try {
			// TODO: don't be linux specific.
			Runtime.getRuntime().exec("rm -rf src/test/resources/src/*.class src/test/resources/out");
		} catch (IOException e) {
			e.printStackTrace();
		}

		final File f = new File("src/test/resources/out");
		f.mkdirs();
	}

	@AfterClass
	public static void cleanUp() {
		try {
			// TODO: don't be linux specific.
			Runtime.getRuntime().exec("rm -rf src/test/resources/src/*.class src/test/resources/out").waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@org.junit.Test
	public void test12() {
		obfuscate("Test1");
		try {
			final Class<?> type = loadClass().loadClass("Test1");
			final Object instance = type.getConstructor().newInstance();
			final Class[] cArg = new Class[1];
         	cArg[0] = int.class;

			int res = (int) type.getMethod("fact", cArg).invoke(instance, 4);
			int res2 = (int) type.getMethod("fact2", cArg).invoke(instance, 4);

			assertEquals(res, res2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void obfuscate(final String name) {
		try {
			Runtime.getRuntime().exec("javac src/test/resources/src/" + name + ".java").waitFor();
			final OpaquePredicateObfuscator opo = new OpaquePredicateObfuscator(
																									"src/test/resources/src/" + name + ".class",
																									"src/test/resources/out/" + name + ".class");
			opo.obfuscate();
		} catch (Exception e) {
			assertTrue("Failed to obfuscate " + name, false);
		}
	}

	public ClassLoader loadClass() {
		try {
			final URL url = new File("src/test/resources/out").toURI().toURL();
			ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
    	return URLClassLoader.newInstance(new URL[]{url}, prevCl);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
