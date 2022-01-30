package OpaquePredicateJava;

import org.objectweb.asm.Opcodes;

public class ObfuscatorUtils {
    
    // This method is inspired by AdviceAdapter.java, in the asm lib source code.
    // This method return the number of element added / removed from the stack
    // after the execution of the opcode.
    // Ex:
    //      ICONST_0 add 1 element on the stack.
    //      POP2 remove 2 elements from the stack.
    public static int stackSizeAfterOpcode(final int opcode) {
        switch (opcode) {
            case Opcodes.NOP:
            case Opcodes.LALOAD:
            case Opcodes.DALOAD:
            case Opcodes.LNEG:
            case Opcodes.DNEG:
            case Opcodes.FNEG:
            case Opcodes.INEG:
            case Opcodes.L2D:
            case Opcodes.D2L:
            case Opcodes.F2I:
            case Opcodes.I2B:
            case Opcodes.I2C:
            case Opcodes.I2S:
            case Opcodes.I2F:
            case Opcodes.ARRAYLENGTH:
                return 0;
            case Opcodes.ACONST_NULL:
            case Opcodes.ICONST_M1:
            case Opcodes.ICONST_0:
            case Opcodes.ICONST_1:
            case Opcodes.ICONST_2:
            case Opcodes.ICONST_3:
            case Opcodes.ICONST_4:
            case Opcodes.ICONST_5:
            case Opcodes.FCONST_0:
            case Opcodes.FCONST_1:
            case Opcodes.FCONST_2:
            case Opcodes.F2L:
            case Opcodes.F2D:
            case Opcodes.I2L:
            case Opcodes.I2D:
            case Opcodes.DUP:
                return 1;
            case Opcodes.LCONST_0:
            case Opcodes.LCONST_1:
            case Opcodes.DCONST_0:
            case Opcodes.DCONST_1:
                return 2;
            case Opcodes.IALOAD:
            case Opcodes.FALOAD:
            case Opcodes.AALOAD:
            case Opcodes.BALOAD:
            case Opcodes.CALOAD:
            case Opcodes.SALOAD:
            case Opcodes.POP:
            case Opcodes.IADD:
            case Opcodes.FADD:
            case Opcodes.ISUB:
            case Opcodes.LSHL:
            case Opcodes.LSHR:
            case Opcodes.LUSHR:
            case Opcodes.L2I:
            case Opcodes.L2F:
            case Opcodes.D2I:
            case Opcodes.D2F:
            case Opcodes.FSUB:
            case Opcodes.FMUL:
            case Opcodes.FDIV:
            case Opcodes.FREM:
            case Opcodes.FCMPL:
            case Opcodes.FCMPG:
            case Opcodes.IMUL:
            case Opcodes.IDIV:
            case Opcodes.IREM:
            case Opcodes.ISHL:
            case Opcodes.ISHR:
            case Opcodes.IUSHR:
            case Opcodes.IAND:
            case Opcodes.IOR:
            case Opcodes.IXOR:
            case Opcodes.MONITORENTER:
            case Opcodes.MONITOREXIT:
                return -1;
            case Opcodes.POP2:
            case Opcodes.LSUB:
            case Opcodes.LMUL:
            case Opcodes.LDIV:
            case Opcodes.LREM:
            case Opcodes.LADD:
            case Opcodes.LAND:
            case Opcodes.LOR:
            case Opcodes.LXOR:
            case Opcodes.DADD:
            case Opcodes.DMUL:
            case Opcodes.DSUB:
            case Opcodes.DDIV:
            case Opcodes.DREM:
                return -2;
            case Opcodes.IASTORE:
            case Opcodes.FASTORE:
            case Opcodes.AASTORE:
            case Opcodes.BASTORE:
            case Opcodes.CASTORE:
            case Opcodes.SASTORE:
            case Opcodes.LCMP:
            case Opcodes.DCMPL:
            case Opcodes.DCMPG:
                return -3;
            case Opcodes.LASTORE:
            case Opcodes.DASTORE:
                return -4;
            // Because *RETURN are not obfuscated, so we don't care.
            case Opcodes.RETURN:
            case Opcodes.IRETURN:
            case Opcodes.FRETURN:
            case Opcodes.ARETURN:
            case Opcodes.LRETURN:
            case Opcodes.DRETURN:
            case Opcodes.ATHROW:
                return 0;
        }

        return 0;
    }
}
