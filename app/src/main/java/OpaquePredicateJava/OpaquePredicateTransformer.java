package OpaquePredicateJava;

import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

public class OpaquePredicateTransformer extends ClassNode {
	private static final int API = Opcodes.ASM9;

	private final List<String> functionsNames;
	private final ClassNode cn;

	public OpaquePredicateTransformer(final ClassNode cn, final List<String> functionsNames) {
		super(API);
		this.cn = cn;
		this.functionsNames = functionsNames;
	}

	public void transform() {
		for (final MethodNode mn : cn.methods) {
			if (!this.functionsNames.contains(mn.name)) {
				continue;
			}

			final InsnList instructions = mn.instructions;
			if (instructions.size() == 0) {
				continue;
			}

			final InsnList newInstructions = new InsnList();
			instructions.forEach(inst -> {
				final int opcode = inst.getOpcode();

				if (isReturnInsn(opcode) || !(inst instanceof InsnNode)) {
					newInstructions.add(inst);
					return;
				}

				// Replace the current instruction the with if/else branching.
				final InsnList list = new InsnList();
				final Label labelFalse = new Label();
				final LabelNode labelNodeFalse = new LabelNode(labelFalse);
				final Label labelEnd = new Label();
				final LabelNode labelNodeEnd = new LabelNode(labelEnd);
				this.generatePredicate(list);
				list.add(new JumpInsnNode(Opcodes.IF_ICMPNE, labelNodeFalse));
				list.add(inst);
				list.add(new JumpInsnNode(Opcodes.GOTO, labelNodeEnd));
				list.add(labelNodeFalse);
				this.generateGarbage(opcode, list);
				list.add(new JumpInsnNode(Opcodes.GOTO, labelNodeEnd));
				list.add(labelNodeEnd);

				newInstructions.add(list);
			});
		}
	}

	private void generatePredicate(final InsnList list) {
		// TODO: For now, we generate the basic 0 == 0 predicate.
		list.add(new InsnNode(Opcodes.ICONST_0));
		list.add(new InsnNode(Opcodes.ICONST_0));
	}

	private void generateGarbage(final int opcode, final InsnList list) {
		// TODO: For now, we just push / pop the same number of stuff on the stack as the opcode does.
		int stackChanges = ObfuscatorUtils.stackSizeAfterOpcode(opcode);

		if (stackChanges == 0) {
			list.add(new InsnNode(Opcodes.NOP));
			return;
		}

		// This opcode are randomly select between all possible opcodes
		// that add/remove 1 element to/from the stack.
		final int garbageOpcode;
		if (stackChanges < 0) {
			garbageOpcode = Opcodes.POP;
		} else {
			garbageOpcode = Opcodes.ICONST_5;
		}
		
		for (int i = 0; i < Math.abs(stackChanges); i++) {
			list.add(new InsnNode(garbageOpcode));
		}
	}

	private boolean isReturnInsn(final int opcode) {
		switch (opcode) {
			case Opcodes.IRETURN:
			case Opcodes.LRETURN:
			case Opcodes.FRETURN:
			case Opcodes.DRETURN:
			case Opcodes.ARETURN:
			case Opcodes.RETURN:
			case Opcodes.ATHROW:
				return true;
			default:
				return false;
		}
	}

}