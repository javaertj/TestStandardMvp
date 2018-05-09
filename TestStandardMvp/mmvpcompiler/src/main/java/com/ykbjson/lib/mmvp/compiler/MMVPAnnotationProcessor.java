package com.ykbjson.lib.mmvp.compiler;


import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.ykbjson.lib.mmvp.annotation.ActionProcess;
import com.ykbjson.lib.mmvp.annotation.MMVPActionProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * 包名：com.ykbjson.lib.mmvp.compiler
 * 描述：mmvp{@link MMVPActionProcessor}注解处理器
 * 创建者：yankebin
 * 日期：2018/5/4
 */
@AutoService(Processor.class)
public class MMVPAnnotationProcessor extends AbstractProcessor {
    private static final String CLASS_NAME_SUFFIX = "_MMVPActionProcessor";
    private static final String METHOD_NAME_SUFFIX = "_Process";
    private static final String OVERRIDE_METHOD_NAME_HANDLE_ACTION = "handleAction";
    private static final String OVERRIDE_METHOD_NAME_GET = "get";

    private Elements elementUtils;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        parseExecutor(roundEnv);
        return true;
    }

    private void parseExecutor(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(MMVPActionProcessor.class);
        for (Element element : elements) {
            // 判断是否Class
            TypeElement typeElement = (TypeElement) element;
            //要生成的类
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(element.getSimpleName() +
                    CLASS_NAME_SUFFIX)//类名
                    .addSuperinterface(ClassName.get("com.ykbjson.lib.mmvp", "IMMVPActionHandler"))//实现的接口
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)//类修饰符
                    .addField(FieldSpec.builder(TypeName.get(typeElement.asType()), "target", Modifier.PRIVATE).build());//成员变量
            //生成的类的构造方法
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)//方法的修饰符
                    .addParameter(TypeName.get(typeElement.asType()), "target")//方法的参数
                    .addStatement("this.$N = $N", "target", "target");//方法的内容

            typeSpecBuilder.addMethod(constructorBuilder.build());//添加到类里
            //target类目标方法获取和参数解析后调用。这里就是遍历target目标类里的方法，看那哪些方法添加了ActionProcess注解，
            // 然后，根据收到的Action，根据方法的注解参数，解析action里的参数，然后调用目标类的方法。
            final List<? extends Element> members = elementUtils.getAllMembers(typeElement);
            final Map<String, String> methodMap = new LinkedHashMap<>();
            for (Element item : members) {
                ActionProcess methodAnnotation = item.getAnnotation(ActionProcess.class);
                if (methodAnnotation == null) {
                    continue;
                }

                final String generatedMethodName = item.getSimpleName().toString() + METHOD_NAME_SUFFIX;
                //保存方法和注解的关系
                methodMap.put(methodAnnotation.value(), generatedMethodName);
                MethodSpec.Builder actionProcessMethodSpecBuilder = MethodSpec.methodBuilder(
                        generatedMethodName)
                        .returns(TypeName.BOOLEAN)
                        .addModifiers(Modifier.PUBLIC);

                //方法必要的唯一参数-MMVPAction
                actionProcessMethodSpecBuilder.addParameter(ParameterSpec.builder(
                        ClassName.get("com.ykbjson.lib.mmvp", "MMVPAction"), "action")
                        .build());
                //如果当前传入的MMVPAction要执行的方法和当前方法不一致，中断执行
                CodeBlock codeBlock = CodeBlock.builder().beginControlFlow("if(!\"" + methodAnnotation.value() +
                        "\".equals(action.getAction().getAction()))")
                        .addStatement("return false")
                        .endControlFlow()
                        .build();
                actionProcessMethodSpecBuilder.addCode(
                        codeBlock
                );

                //获取和处理方法参数列表
                ExecutableElement method = (ExecutableElement) item;//方法
                List<? extends VariableElement> parameters = method.getParameters();//方法的参数
                StringBuilder parametersBuffer = new StringBuilder();
                //参数集合,需要参数才去解析参数，这下面生成的代码和MMVPArtist里的execute方法里的代码非常类似
                if (!parameters.isEmpty()) {
                    actionProcessMethodSpecBuilder.addStatement("$T  paramList= new ArrayList<>()", ArrayList.class);
                    if (methodAnnotation.needActionParam()) {
                        if (methodAnnotation.needTransformAction()) {
                            actionProcessMethodSpecBuilder.addStatement(
                                    "action = action.transform()"
                            );
                        }

                        actionProcessMethodSpecBuilder.addStatement(
                                "paramList.add(action)"
                        );
                    }
                    if (methodAnnotation.needActionParams()) {
                        actionProcessMethodSpecBuilder.addStatement(
                                //这里其实可以使用JavaPoet的beginControlFlow来优雅地实现for循环
                                "if(null != action.getParams() && !action.getParams().isEmpty()) {\n" +
                                        "for (String key : action.getParams().keySet()) {\n" +
                                        "     paramList.add(action.getParam(key));\n" +
                                        "}" +
                                        "}"
                        );
                    }

                    for (int i = 0; i < parameters.size(); i++) {
                        parametersBuffer.append(
                                "(" + parameters.get(i).asType() + ")" + "paramList.get(" + i + ")");
                        if (i != parameters.size() - 1) {
                            parametersBuffer.append(",");
                        }
                    }
                }
                //这里生成的代码类似 target.findViewById(-122443433)
                actionProcessMethodSpecBuilder.addStatement("target." +
                        method.getSimpleName().toString() +
                        "(" + parametersBuffer.toString() + ")");

                actionProcessMethodSpecBuilder.addStatement("return true");
                typeSpecBuilder.addMethod(actionProcessMethodSpecBuilder.build());
            }
            //重载IMMVPActionHandler方法handleAction
            MethodSpec.Builder overrideMethodSpecBuilder = MethodSpec.methodBuilder(OVERRIDE_METHOD_NAME_HANDLE_ACTION)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.BOOLEAN);

            overrideMethodSpecBuilder.addParameter(ParameterSpec.builder(
                    ClassName.get("com.ykbjson.lib.mmvp", "MMVPAction"), "action")
                    .addAnnotation(ClassName.get("android.support.annotation", "NonNull"))
                    .build());
            //由于无法预知即将调用的方法，只能把重写的方法全部执行一遍，重写方法里有判断可以避免错误执行,并且只要有某个方法返回了true，后续方法将不再执行.
            // 或许这样也不比反射执行的风险小吧.
            int index = 0;
            StringBuilder resultBuilder = new StringBuilder();
            for (String key : methodMap.keySet()) {
                resultBuilder.append(methodMap.get(key) + "(action)" + (index != methodMap.keySet().size() - 1 ? "||" : ""));
                index++;
            }
            overrideMethodSpecBuilder.addStatement("return " + (resultBuilder.length() == 0 ? "false" : resultBuilder.toString()));
            typeSpecBuilder.addMethod(overrideMethodSpecBuilder.build());

            //重载IMMVPActionHandler方法get
            overrideMethodSpecBuilder = MethodSpec.methodBuilder(OVERRIDE_METHOD_NAME_GET)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.get("com.ykbjson.lib.mmvp", "IMMVPActionHandler"));
            overrideMethodSpecBuilder.addStatement("return this");
            typeSpecBuilder.addMethod(overrideMethodSpecBuilder.build());

            //生成java文件
            JavaFile javaFile = JavaFile.builder(getPackageName(typeElement), typeSpecBuilder.build())
                    .addFileComment(" Generated code from MMVP. Do not modify! ")
                    .build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new LinkedHashSet<>();
        supportedAnnotationTypes.add(MMVPActionProcessor.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }
}
