import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import OpaquePredicateJava.OpaquePredicateObfuscator;

public class Test {

	public static void runCmd(final String cmd) {
		try {
			Runtime.getRuntime().exec(cmd).waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cleanTmpFiles() {
		Stream.of(new File("src/test/resources/src/")
			.listFiles())
			.filter(file -> !file.isDirectory() && file.getName().endsWith(".class"))
			.forEach(file -> file.delete());

		try {
			FileUtils.deleteDirectory(new File("src/test/resources/out"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUp() {
		// Clean and create temporary folder.
		cleanTmpFiles();
		final File f = new File("src/test/resources/out");
		f.mkdirs();

		// Compile all java files in src/test/resources/src
		Stream.of(new File("src/test/resources/src/")
				.listFiles())
				.filter(file -> !file.isDirectory())
				.forEach(file -> {
					runCmd("javac " + file);
				});
	}

	@AfterClass
	public static void cleanUp() {
		cleanTmpFiles();
	}

	@org.junit.Test
	public void testRecursive() {
		obfuscate("TestRecursive", "fact");
		try {
			final Class<?> type = loadClass().loadClass("TestRecursive");
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

	@org.junit.Test
	public void TestSimpleCondition() {
		obfuscate("TestSimpleCondition", "majeur");
		try {
			final Class<?> type = loadClass().loadClass("TestSimpleCondition");
			final Object instance = type.getConstructor().newInstance();
			final Class[] cArg = new Class[1];
         	cArg[0] = int.class;

			String res = (String) type.getMethod("majeur", cArg).invoke(instance, 4);
			String res2 = (String) type.getMethod("majeur2", cArg).invoke(instance, 4);

			assertEquals(res, res2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@org.junit.Test
	public void TestSumLoop() {
		obfuscate("TestSumLoop", "compute");
		try {
			final Class<?> type = loadClass().loadClass("TestSumLoop");
			final Object instance = type.getConstructor().newInstance();
			final Class[] cArg = new Class[0];

			int res = (int) type.getMethod("compute", cArg).invoke(instance);
			int res2 = (int) type.getMethod("compute2", cArg).invoke(instance);

			assertEquals(res, res2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@org.junit.Test
	public void TestMonteCarlo() {
		obfuscate("TestMonteCarlo", "monte");
		try {
			final Class<?> type = loadClass().loadClass("TestMonteCarlo");
			final Object instance = type.getConstructor().newInstance();
			final Class[] cArg = new Class[0];

			double res = (double) type.getMethod("monte", cArg).invoke(instance);
			double res2 = (double) type.getMethod("monte2", cArg).invoke(instance);

			// MonteCarlo is an approximation based on randomness.
			assertTrue(Math.abs(res - res2) < 0.01);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void obfuscate(final String name, final String functionName) {
		try {
			Runtime.getRuntime().exec("javac src/test/resources/src/" + name + ".java").waitFor();
			final OpaquePredicateObfuscator opo = new OpaquePredicateObfuscator("src/test/resources/src/" + name + ".class",
																				"src/test/resources/out/" + name + ".class",
																				Arrays.asList(functionName));
			opo.obfuscate();
		} catch (Exception e) {
			e.printStackTrace();
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
