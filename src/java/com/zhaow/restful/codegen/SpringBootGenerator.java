package com.zhaow.restful.codegen;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.zhaow.utils.ToolkitUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class SpringBootGenerator extends AnAction {
    Project project;

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

//        e.getData(PlatformDataKeys.VIRTUAL_FILE) ;
// instanceof VirtualDirectoryImpl file://E:/project/module/src/test
        String currentPathName = e.getData(PlatformDataKeys.VIRTUAL_FILE).getName();

        PsiElement psiElement = e.getData(PlatformDataKeys.PSI_ELEMENT);

        //  instanceof PsiJavaDirectoryImpl
        // PsiDirectory:E:\project\module\src\test

//        e.getData(PlatformDataKeys.NAVIGATABLE); 同 PSI_ELEMENT

//        e.getData(PlatformDataKeys.SEARCH_INPUT_TEXT)
//        e.getData(PlatformDataKeys.e.getData(PlatformDataKeys.SPEED_SEARCH_COMPONENT))

        if (editor == null) {
            return;
        }
        String content = editor.getDocument().getText();




//        createPackage();
//        createFile()

        String currentPath = getCurrentPath(e);
/*        String basePath = currentPath.replace("contract/" + className + ".java", "");
        String basePackage = getPackageName(basePath);
        String modelName = className.substring(0, className.indexOf("Contract"));*/


    }


    public void createFile(String fileName,String content) {
        PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText(fileName, FileTypes.PLAIN_TEXT, content);

        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
        PsiFile file = directory.findFile(fileName);
        if (file == null) {
            ToolkitUtil.runWriteAction(() -> directory.add(psiFile));
        } else {
            ToolkitUtil.runWriteAction(() -> {
                file.delete();
                directory.add(psiFile);
            });
        }
    }

    private String genFromTemplate(String templateName, Map<String ,String > dataMap) {

        VelocityContext context = new VelocityContext();

//        context.put("services", services);

        String path = "template/";
        String templatePath = path + templateName;
        InputStream input = getClass().getClassLoader().getResourceAsStream(templatePath);

        VelocityEngine engine = new VelocityEngine();
        Properties props = new Properties();
        props.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        props.put("runtime.log.logsystem.log4j.category", "velocity");
        props.put("runtime.log.logsystem.log4j.logger", "velocity");

        engine.init(props);
        StringWriter writer = new StringWriter();
        engine.evaluate(context, writer, "REST", new InputStreamReader(input));

        return writer.toString().replace("\n", "").replace("\r", "");
    }


    // 首先判断要生成的目录和文件和目录是否存在，如果任何一个存在，则退出

    // 创建目录，文件
    private void createController(String basePackage, String path, String modelName) {

        createFile(modelName +"Controller" , genFromTemplate("controller",new HashMap<>())) ;
    }

    private void refreshProject(AnActionEvent e) {
        e.getProject().getBaseDir().refresh(false, true);
    }

    private String getCurrentPath(AnActionEvent e) {
        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (currentFile != null) {
            return currentFile.getPath();
        }
        return null;
    }
}
