package com.seven.compiler;

import com.google.auto.service.AutoService;
import com.seven.annotation.BindView;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by sunyuanming on 17-11-25.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.seven.annotation.BindView")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ViewInjectProcessor extends AbstractProcessor {
    //存放所有的注解
    Map<String, List<VariableInfo>> classMap = new HashMap<>();
    //存放class对应的typeelement
    Map<String, TypeElement> typeMap = new HashMap<>();
    private Filer filer;
    Elements elements;
    private ProcessingEnvironment environment;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        environment=processingEnvironment;
        filer = processingEnvironment.getFiler();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        getAllInfo(roundEnvironment);
        writeToFile();
        return true;
    }

    private void writeToFile() {

        for (String className : classMap.keySet()) {
            TypeElement typeElement = typeMap.get(className);
            MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(TypeName.get(typeElement.asType()), "activity").build());
            List<VariableInfo> variableInfoList = classMap.get(className);
            for (VariableInfo info : variableInfoList) {
                VariableElement variableElement = info.getVariableElement();
                String variableName = variableElement.getSimpleName().toString();
                String variableFullName = variableElement.asType().toString();
                constructor.addStatement("activity.$L=($L)activity.findViewById($L)", variableName, variableFullName, info.getViewId());
                TypeSpec typeSpec = TypeSpec.classBuilder(typeElement.getSimpleName() + "$$ViewInjector")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(constructor.build())
                        .build();
                String packageFullName = elements.getPackageOf(typeElement).getQualifiedName().toString();
                JavaFile javaFile = JavaFile.builder(packageFullName, typeSpec).build();
                try {
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getAllInfo(RoundEnvironment environment) {
        classMap.clear();
        typeMap.clear();
        Set<? extends Element> elements = environment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elements) {
            int viewId = element.getAnnotation(BindView.class).value();
            VariableElement variableElement = (VariableElement) element;
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            String classname = typeElement.getQualifiedName().toString();
            List<VariableInfo> list = classMap.get(classname);
            if (list == null) {
                list = new ArrayList<>();
                classMap.put(classname, list);
                typeMap.put(classname, typeElement);
            }
            VariableInfo info = new VariableInfo();
            info.setVariableElement(variableElement);
            info.setViewId(viewId);
            list.add(info);
            System.out.print(viewId + "::" + classname);
        }

    }


}
