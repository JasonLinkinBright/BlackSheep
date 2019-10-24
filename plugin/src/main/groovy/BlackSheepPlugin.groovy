package com.lsj.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class BlackSheepPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        System.out.println("========================");
        System.out.println("Hello BlackSheepPlugin!");
        System.out.println("========================");

        AppExtension appExtension = project.getProperties().get("android");
        appExtension.registerTransform(new BlackSheepPlugin(project), Collections.EMPTY_LIST);
    }
}