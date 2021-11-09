package OpaquePredicateJava;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class OpaquePredicateObfuscator {

    final String inputFilename;
    final String outputFilename;
    final List<String> functionsNames;
    
    public OpaquePredicateObfuscator(final String inputFilename, final String outputFilename, final List<String> functionsNames) {
        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;
        if (functionsNames == null) {
            this.functionsNames = new ArrayList<String>();
        } else {
            this.functionsNames = functionsNames;
        }
    }

    public OpaquePredicateObfuscator(final String inputFilename, final String outputFilename) {
        this(inputFilename, outputFilename, null);
    }

    public void obfuscate() throws FileNotFoundException, IOException {
        final ClassReader classReader = new ClassReader(new FileInputStream(this.inputFilename));
        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        final OpaquePredicateVisitor opv = new OpaquePredicateVisitor(classWriter, this.functionsNames);
        classReader.accept(opv, 0);
        final FileOutputStream os = new FileOutputStream(new File(this.outputFilename));
        os.write(classWriter.toByteArray());
        os.close();
    }
}
