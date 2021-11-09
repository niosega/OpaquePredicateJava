package OpaquePredicateJava;

import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class OpaquePredicateVisitor extends ClassVisitor {
    private static final int API = Opcodes.ASM9;

    private final List<String> functionsNames;

    public OpaquePredicateVisitor(final ClassVisitor classVisitor, final List<String> functionsNames) {
        super(API, classVisitor);
        this.functionsNames = functionsNames;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String descriptor,
                                     final String signature, final String[] exceptions) {
        if (this.functionsNames.isEmpty() || this.functionsNames.contains(name)) {
            MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);

            return new MethodVisitor(API, methodVisitor) {

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.ICONST_1) {
                        final Label labelFalse = new Label();
                        final Label labelEnd = new Label();
                        this.generateBranching(mv, labelFalse);
                        mv.visitInsn(opcode);
                        mv.visitJumpInsn(Opcodes.GOTO, labelEnd);
                        mv.visitLabel(labelFalse);                        
                        this.generateGarbage(mv);
                        mv.visitLabel(labelEnd);
                        return;
                    }

                    super.visitInsn(opcode);
                }

                private void generateGarbage(final MethodVisitor mv) {
                    mv.visitInsn(Opcodes.ICONST_3);
                }

                private void generateBranching(final MethodVisitor mv, final Label labelFalse) {
                    mv.visitInsn(Opcodes.ICONST_0);
                    mv.visitInsn(Opcodes.ICONST_0);
                    mv.visitJumpInsn(Opcodes.IF_ICMPNE, labelFalse);
                }
            };
        } else
            return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

}