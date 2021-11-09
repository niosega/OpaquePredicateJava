package OpaquePredicateJava;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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
			final OpaquePredicateObfuscator opo = new OpaquePredicateObfuscator(inputFilename, outputFilename, functionsNames);
			opo.obfuscate();
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
