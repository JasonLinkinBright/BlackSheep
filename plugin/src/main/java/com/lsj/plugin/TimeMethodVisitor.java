package com.lsj.plugin;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author shijun_lin
 * @date 2019/10/24
 */
public class TimeMethodVisitor extends MethodVisitor {
    private boolean inject = false;
    private String methodName = "";

    public TimeMethodVisitor(MethodVisitor mv,String name) {
        super(Opcodes.ASM5, mv);
        methodName = name;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (Type.getDescriptor(TimeCost.class).equals(descriptor)) {
            inject = true;
        }
        return super.visitAnnotation(descriptor, visible);
    }

    //开始访问方法
    @Override
    public void visitCode() {
        if (inject) {
            super.visitCode();
            mv.visitLdcInsn("methodName");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lsj/plugin/TimeCostCache","setStartTime", "(Ljava/lang/String;J)V", false);

            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("========start=========");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    "(Ljava/lang/String;)V", false);

            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lsj/plugin/TimeCostCache", "setStartTime",
                    "(Ljava/lang/String;J)V", false);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (inject) {
            //判断内部操作指令
            //当前指令是RETURN，表示方法内部的代码已经执行完
            if (opcode == Opcodes.RETURN) {
//                mv.visitLdcInsn("methodName");
//                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lsj/plugin/TimeCostCache", "setEndTime",
//                        "(Ljava/lang/String;J)V", false);
//
//                mv.visitLdcInsn("methodName");
//                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lsj/plugin/TimeCostCache", "getCostTime",
//                        "(Ljava/lang/String;)Ljava/lang/String;", false);
//                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
//                mv.visitVarInsn(Opcodes.LLOAD, 1);
//                mv.visitInsn(Opcodes.LSUB);
//                mv.visitVarInsn(Opcodes.LSTORE, 3);
//                Label l3 = new Label();
//                mv.visitLabel(l3);
//                mv.visitLineNumber(20, l3);
//                mv.visitLdcInsn("TAG");
//                mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
//                mv.visitInsn(Opcodes.DUP);
//                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
//                mv.visitLdcInsn("interval:");
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
//                mv.visitVarInsn(Opcodes.LLOAD, 3);
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
//                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
//                mv.visitInsn(Opcodes.POP);
                mv.visitLdcInsn(methodName);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lsj/plugin/TimeCostCache", "setEndTime",
                        "(Ljava/lang/String;J)V", false);

                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn(methodName);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/lsj/plugin/TimeCostCache", "getCostTime",
                        "(Ljava/lang/String;)Ljava/lang/String;", false);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                        "(Ljava/lang/String;)V", false);

                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn("========end=========");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                        "(Ljava/lang/String;)V", false);
            }
            super.visitInsn(opcode);
        }

    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        //访问结束
    }




}
