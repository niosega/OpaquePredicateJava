package OpaquePredicateJava;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "obfuscate-class",
		 mixinStandardHelpOptions = true,
		 version = "0.1",
		 description = "Obfuscate a .class using Opaque Predicate.")
public class App implements Callable<Integer> {

	@Parameters(index = "0", description = "The .class to obfuscate.")
	private String inputFilename;

	@Parameters(index = "1", description = "The obfuscated .class.")
	private String outputFilename;

	@Option(names = {"--only"}, description = "Only obfuscate function with specified names.")
	private List<String> functionsNames = new ArrayList<String>();

	@Override
	public Integer call() throws Exception {
		try {
			final ClassReader classReader = new ClassReader(new FileInputStream(inputFilename));
			final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			final OpaquePredicateVisitor opv = new OpaquePredicateVisitor(classWriter, functionsNames);
			classReader.accept(opv, 0);
			final FileOutputStream os = new FileOutputStream(new File(outputFilename));
			os.write(classWriter.toByteArray());
			os.close();

			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new App()).execute(args);
		System.exit(exitCode);
	}
}
