import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import static jdk.internal.org.objectweb.asm.Opcodes.GETSTATIC;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * @author shijun_lin
 * @date 2019/10/24
 */
public class TimeMethodVisitor extends MethodVisitor {
    public TimeMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }

    @Override
    public void visitCode() {
        //方法体内开始时调用
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("========start=========");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        super.visitCode();

    }
    @Override
    public void visitInsn(int opcode) {
        //每执行一个指令都会调用
        if (opcode == Opcodes.RETURN) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("========end=========");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
        super.visitInsn(opcode);
    }


}
