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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

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
        // Read the input class file.
        final ClassNode cn = new ClassNode(Opcodes.ASM9);
        final ClassReader classReader = new ClassReader(new FileInputStream(this.inputFilename));
        classReader.accept(cn, 0);

        // Do the tranformation.
        final OpaquePredicateTransformer opt = new OpaquePredicateTransformer(cn, functionsNames);
        opt.transform();

        // Write the obfuscated class file.
        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(classWriter);
        final FileOutputStream os = new FileOutputStream(new File(this.outputFilename));
        os.write(classWriter.toByteArray());
        os.close();
    }
}
