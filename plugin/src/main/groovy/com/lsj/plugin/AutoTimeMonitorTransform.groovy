package com.lsj.plugin

import com.android.build.api.transform.*;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.apache.commons.io.IOUtils

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES


/**
 * @author shijun_lin* @date 2019/10/24
 */
public class AutoTimeMonitorTransform extends Transform {

    @Override
    public String getName() {
        return SettingConst.TRANSFORM_NAME;
    }

    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
    // 这样确保其他类型的文件不会传入
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }
    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
//        long startTime = System.currentTimeMillis()
//        println '----------- startTime <' + startTime + '> -----------'
//        //拿到所有的class文件
//        Collection<TransformInput> inputs = transformInvocation.inputs;
//        TransformOutputProvider outputProvider = transformInvocation.outputProvider;
//        if (outputProvider != null) {
//            outputProvider.deleteAll()
//        }
//        //遍历inputs Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
//        inputs.each { TransformInput input ->
//            //遍历directoryInputs(文件夹中的class文件) directoryInputs代表着以源码方式参与项目编译的所有目录结构及其目录下的源码文件
//            // 比如我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
//            input.directoryInputs.each { DirectoryInput directoryInput ->
//                //文件夹中的class文件
//                handDirectoryInput(directoryInput, outputProvider)
//            }
//            //遍历jar包中的class文件 jarInputs代表以jar包方式参与项目编译的所有本地jar包或远程jar包
//            input.jarInputs.each { JarInput jarInput ->
//                //处理jar包中的class文件
//                handJarInput(jarInput, outputProvider)
//            }
//        }
        println '//===============asm visit start===============//'

        def startTime = System.currentTimeMillis()

        inputs.each { TransformInput input ->

            input.directoryInputs.each { DirectoryInput directoryInput ->

                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                        def name = file.name
                        if (name.endsWith(".class") && !name.startsWith("R\$") &&
                                !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {

                            println name + ' is changing...'

                            ClassReader cr = new ClassReader(file.bytes)
                            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                            ClassVisitor cv = new TimeCostClassVisitor(cw)

                            cr.accept(cv, EXPAND_FRAMES)

                            byte[] code = cw.toByteArray()

                            FileOutputStream fos = new FileOutputStream(
                                    file.parentFile.absolutePath + File.separator + name)
                            fos.write(code)
                            fos.close()
                        }
                    }
                }

                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)


                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.each { JarInput jarInput ->
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                FileUtils.copyFile(jarInput.file, dest)
            }
        }

        def cost = (System.currentTimeMillis() - startTime) / 1000

        println "plugin cost $cost secs"
        println '//===============asm visit end===============//'
    }

    //遍历directoryInputs  得到对应的class  交给ASM处理
    private static void handDirectoryInput(DirectoryInput input, TransformOutputProvider outputProvider) {
        //是否是文件夹
        if (input.file.isDirectory()) {
            //列出目录所有文件（包含子文件夹，子文件夹内文件）
            input.file.eachFileRecurse { File file ->
                String name = file.name
                //需要插桩class 根据自己的需求来------------- 这里判断是否是我们自己写的Application
                if ("MainActivity.class".equals(name)) {
                    println("==== begin changing MainAcitivity.class")
                    ClassReader classReader = new ClassReader(file.bytes)
                    //传入COMPUTE_MAXS  ASM会自动计算本地变量表和操作数栈
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    //创建类访问器   并交给它去处理
                    ClassVisitor classVisitor = new TimeClassVisitor(classWriter)
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    FileOutputStream fos = new FileOutputStream(file.parentFile.absolutePath + File.separator + name)
                    fos.write(code)
                    fos.close()
                }
            }
        }
        //处理完输入文件后把输出传给下一个文件
        def dest = outputProvider.getContentLocation(input.name, input.contentTypes, input.scopes, Format.DIRECTORY)
        FileUtils.copyDirectory(input.file, dest)
    }
    //遍历jarInputs 得到对应的class 交给ASM处理
    private static void handJarInput(JarInput jarInput, TransformOutputProvider outputProvider) {
        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
            //重名名输出文件,因为可能同名,会覆盖
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            JarFile jarFile = new JarFile(jarInput.file)
            Enumeration enumeration = jarFile.entries()
            File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            //避免上次的缓存被重复插入
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
            //用于保存
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = jarFile.getInputStream(jarEntry)
                //需要插桩class 根据自己的需求来-------------
                if ("androidx/fragment/app/FragmentActivity.class".equals(entryName)) {
                    //class文件处理
                    println '----------- jar class  <' + entryName + '> -----------'
                    jarOutputStream.putNextEntry(zipEntry)
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    //创建类访问器   并交给它去处理
                    ClassVisitor cv = new TimeClassVisitor(classWriter)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            //结束
            jarOutputStream.close()
            jarFile.close()
            //获取output目录
            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }
}
