package OpaquePredicateJava;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class OpaquePredicateVisitor extends ClassVisitor {

    private static final String TRANSFORM_METHOD_NAME = "fact2";

    public OpaquePredicateVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals(TRANSFORM_METHOD_NAME)) {
            MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);

            return new MethodVisitor(Opcodes.ASM7, methodVisitor) {

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.ICONST_1) {
                        final Label labelFalse = new Label();
                        final Label labelEnd = new Label();
                        mv.visitInsn(Opcodes.ICONST_0);
                        mv.visitInsn(Opcodes.ICONST_0);
                        mv.visitJumpInsn(Opcodes.IF_ICMPNE, labelFalse);
                        mv.visitInsn(opcode);
                        mv.visitJumpInsn(Opcodes.GOTO, labelEnd);
                        mv.visitLabel(labelFalse);                        
                        mv.visitInsn(Opcodes.ICONST_3);
                        mv.visitLabel(labelEnd);
                        return;
                    }

                    super.visitInsn(opcode);
                }
            };
        } else
            return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

}