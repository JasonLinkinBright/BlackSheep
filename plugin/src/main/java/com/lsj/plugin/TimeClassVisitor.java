package com.lsj.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author shijun_lin
 * @date 2019/10/24
 */
public class TimeClassVisitor extends ClassVisitor {
    private String mClassName;
    public TimeClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println("TimeClassVisitor : visit -----> started:" + name);
        this.mClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }
    //定义一个方法， 返回的MethodVisitor用于生成方法相关的信息
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if ((this.mClassName).endsWith("Activity")) {
            if ("onCreate".equals(name)) {
                //处理onCreate
                System.out.println("TimeClassVisitor : visitMethod method ----> " + name);
                return new TimeMethodVisitor(mv);
            }
        }
        return mv;
    }
    //访问结束
    @Override
    public void visitEnd() {
        System.out.println("TimeClassVisitor : visit -----> end");
        super.visitEnd();
    }
}
