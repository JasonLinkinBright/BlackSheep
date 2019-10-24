import com.android.build.api.transform.Context;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;


/**
 * @author shijun_lin
 * @date 2019/10/24
 */
public class AutoTimeMonitorTransform extends Transform {

    @Override
    public String getName() {
        return SettingConst.TRANSFORM_NAME;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        System.out.println("BlackSheepPlugin visit start");
        long startTime = System.currentTimeMillis();
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        //删除之前的输出
        if (outputProvider != null)
            outputProvider.deleteAll();
        //遍历inputs
        for(TransformInput input:inputs){
            for(DirectoryInput directoryInput: input.getDirectoryInputs()){
                // 处理文件目录下的 class 文件
//                handleDirectoryInput(directoryInput, outputProvider);
            }

            for(JarInput jarInput:input.getJarInputs()){
                //处理jarInputs
//                handleJarInputs(jarInput, outputProvider);
            }
        }
        long cost = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println("BlackSheepPlugin visit end");
        System.out.println("BlackSheepPlugin cost ： " + cost);
    }
}
