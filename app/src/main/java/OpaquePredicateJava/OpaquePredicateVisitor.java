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
				public void visitJumpInsn(final int opcode, final Label label) {
					// TODO: obfuscate jump insn.
				}

				@Override
				public void visitVarInsn(final int opcode, final int var) {
					// TODO: obfuscate var insn.
				}

				@Override
				public void visitInsn(final int opcode) {
					this.generateOpaquePredicate(opcode);
				}

				private void generateOpaquePredicate(final int opcode) {
					// Our goal is to generate a code of this form :
					//
					// if (predicate) {
					// 	original code
					// } else {
					// 	garbage
					// }
					//
					// which can be written in pseudo-code as:
					//
					// if false goto labelFalse
					//	    originalCode
					//      goto LabelEnd
					// labelFalse:
					//      garbage
					// labelEnd:
					if (!isReturnInsn(opcode)) {
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
					// TODO: For now, we just push 3 on the stack.
					mv.visitInsn(Opcodes.ICONST_3);
				}

				private void generateBranching(final MethodVisitor mv, final Label labelFalse) {
					// TODO: For now, we assume that the original code is in the if block.
					mv.visitInsn(Opcodes.ICONST_0);
					mv.visitInsn(Opcodes.ICONST_0);
					mv.visitJumpInsn(Opcodes.IF_ICMPNE, labelFalse);
				}
			};
		} else
			return super.visitMethod(access, name, descriptor, signature, exceptions);
	}

	private boolean isReturnInsn(final int opcode) {
		switch (opcode) {
			case Opcodes.IRETURN:
			case Opcodes.LRETURN:
			case Opcodes.FRETURN:
			case Opcodes.DRETURN:
			case Opcodes.ARETURN:
			case Opcodes.RETURN:
				return true;
			default:
				return false;
		}
	}

}