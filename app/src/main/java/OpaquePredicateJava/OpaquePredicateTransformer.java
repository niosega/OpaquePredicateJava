package OpaquePredicateJava;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

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
			// Filter methods if user asks us to obfuscate only specific methods.
			if (!this.functionsNames.contains(mn.name)) {
				continue;
			}

			// Choose predicate variable.
			final LocalVariableNode predicateVar = this.choosePredicateVariable(mn);
			if (predicateVar == null) {
				System.out.println(
					String.format(
						"No suitable predicate variable found, method %s can not be obfuscated. Method ignored.",
						mn.name
					)
				);

				continue;
			}

			// If there are no instructions, there is nothing to do.
			final InsnList instructions = mn.instructions;
			if (instructions.size() == 0) {
				continue;
			}

			// Iterate over method instructions and obfuscate suitable one.
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
				this.generatePredicate(predicateVar, labelNodeFalse, list);
				list.add(inst);
				list.add(new JumpInsnNode(Opcodes.GOTO, labelNodeEnd));
				list.add(labelNodeFalse);
				this.generateGarbage(opcode, list);
				list.add(new JumpInsnNode(Opcodes.GOTO, labelNodeEnd));
				list.add(labelNodeEnd);

				newInstructions.add(list);
			});

			// Replace the current instruction by the obfuscated ones.
			mn.instructions = newInstructions;
		}
	}

	private LocalVariableNode choosePredicateVariable(final MethodNode mn) {
		if (mn.localVariables != null) {
			final List<LocalVariableNode> possibleLocals = new ArrayList<LocalVariableNode>();
			for (final LocalVariableNode lv : mn.localVariables) {
				// TODO: For now, we only supported int variable in predicate.
				// but it is possible to use variable of any kind, we just have
				// to adjust how predicate computation is performed.
				if (lv.desc.equals("I")) {
					possibleLocals.add(lv);
				}
			}

			if (!possibleLocals.isEmpty()) {
				return possibleLocals.get(new Random().nextInt(possibleLocals.size()));
			}
		}

		// TODO: improve predicate variable choice, by adding class field or system lib call.
		return null;
	}

	private void generatePredicate(final LocalVariableNode predicateVar, final LabelNode labelFalse, final InsnList list) {
		// TODO: For now, we generate the basic x*x < 0 predicate.
		list.add(new VarInsnNode(Opcodes.ILOAD, predicateVar.index));
		list.add(new VarInsnNode(Opcodes.ILOAD, predicateVar.index));
		list.add(new InsnNode(Opcodes.IMUL));
		list.add(new JumpInsnNode(Opcodes.IFLT, labelFalse));
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