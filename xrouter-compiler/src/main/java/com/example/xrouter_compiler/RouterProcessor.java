package com.example.xrouter_compiler;

import com.example.xrouter_annotations.Route;
import com.example.xrouter_annotations.RouteMeta;
import com.example.xrouter_annotations.RouteType;
import com.example.xrouter_compiler.utils.Constant;
import com.example.xrouter_compiler.utils.Log;
import com.example.xrouter_compiler.utils.StringUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.example.xrouter_compiler.utils.Constant.ACTIVITY;
import static com.example.xrouter_compiler.utils.Constant.IPROVIDER;
import static com.example.xrouter_compiler.utils.Constant.IPROVIDER_GROUP;
import static com.example.xrouter_compiler.utils.Constant.ROUTE_ROOT_PACKAGE;
import static javax.lang.model.element.Modifier.PUBLIC;


@AutoService(Processor.class)
@SupportedOptions(Constant.ARGUMENTS_NAME)
@SupportedAnnotationTypes(Constant.ANNOTATION_TYPE_ROUTE)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RouterProcessor extends AbstractProcessor {


    private Log log;
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filerUtils;
    private String moduleName;
    private TypeMirror iProvider;
    private TypeMirror iProviderGroup;

    private Map<String, List<RouteMeta>> groupMap = new HashMap<>();

    private Map<String, String> rootMap = new TreeMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        log = Log.newLog(processingEnv.getMessager());
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filerUtils = processingEnv.getFiler();

        Map<String, String> options = processingEnv.getOptions();
        if (options != null && !options.isEmpty()) {
            moduleName = options.get(Constant.ARGUMENTS_NAME);
        }
        if (moduleName == null || moduleName.isEmpty()) {
            throw new RuntimeException("Not set processor moduleName option");
        }

        iProvider = elementUtils.getTypeElement(IPROVIDER).asType();

        log.i("init RouterProcessor " + moduleName + " success !");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations != null && !annotations.isEmpty()) {
            Set<? extends Element> rootElements = roundEnv.getElementsAnnotatedWith(Route.class);
            if (rootElements != null && !rootElements.isEmpty()) {
                processorRoute(rootElements);
            }
            return true;
        }
        return false;
    }

    private void processorRoute(Set<? extends Element> rootElements) {
        TypeElement activity = elementUtils.getTypeElement(ACTIVITY);
        iProviderGroup = elementUtils.getTypeElement(IPROVIDER_GROUP).asType();

        for (Element element : rootElements) {
            RouteMeta routeMeta;
            TypeMirror typeMirror = element.asType();
            log.i("Route class:" + typeMirror.toString());
            Route route = element.getAnnotation(Route.class);
            if (typeUtils.isSubtype(typeMirror, activity.asType())) {
                routeMeta = new RouteMeta(route, element, RouteType.ACTIVITY);
            } else if (typeUtils.isSubtype(typeMirror, iProvider)) {
                routeMeta = new RouteMeta(route, element, RouteType.PROVIDER);
            } else {
                throw new RuntimeException("Just support Activity now");
            }
            categories(routeMeta);
        }
        TypeElement iRouteGroup = elementUtils.getTypeElement(Constant.IROUTE_GROUP);
        TypeElement iRouteRoot = elementUtils.getTypeElement(Constant.IROUTE_ROOT);

        generatedGroup(iRouteGroup);

        generatedRoot(iRouteRoot, iRouteGroup);

        generatedProvider();

    }

    private void categories(RouteMeta routeMeta) {
        if (routeVerify(routeMeta)) {
            log.i("Group :" + routeMeta.getGroup() + " path = " + routeMeta.getPath());
            List<RouteMeta> routeMetasMap = groupMap.get(routeMeta.getGroup());
            if (routeMetasMap == null || routeMetasMap.isEmpty()) {
                routeMetasMap = new ArrayList<>();
                routeMetasMap.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetasMap);
            } else {
                routeMetasMap.add(routeMeta);
            }
        } else {
            log.i("Group info error:" + routeMeta.getPath());
        }
    }

    private boolean routeVerify(RouteMeta routeMeta) {
        String path = routeMeta.getPath();
        String group = routeMeta.getGroup();

        if (StringUtils.isEmpty(path)) {
            return false;
        }

        if (group == null || group.isEmpty()) {
            //it can be @path("/group/xxx") or @path("group/xxx")
            int startIndex = path.startsWith("/") ? 1 : 0;
            int endIndex = path.indexOf("/", startIndex);
            String defaultGroup = path.substring(startIndex, endIndex);
            routeMeta.setGroup(defaultGroup);
        }
        return true;
    }


//    public class Router_Group_userservice implements IRouteGroup {
//        @Override
//        public void loadInto(Map<String, RouteMeta> atlas) {
//            atlas.put("/userservice/userservice", RouteMeta.build(RouteType.PROVIDER, UserServiceImpl.class, "/userservice/userservice"));
//        }
//    }
    private void generatedGroup(TypeElement iRouteGroup) {
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class)
        );
        ParameterSpec altas = ParameterSpec.builder(parameterizedTypeName, "atlas").build();

        for (Map.Entry<String, List<RouteMeta>> entry : groupMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("loadInto")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(altas);
            String groupName = entry.getKey();
            List<RouteMeta> groupData = entry.getValue();
            for (RouteMeta routeMeta : groupData) {
                methodBuilder.addStatement("atlas.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S))",
                        routeMeta.getPath(),
                        ClassName.get(RouteMeta.class),
                        ClassName.get(RouteType.class),
                        ClassName.get((TypeElement) routeMeta.getElement()),
                        routeMeta.getPath()
                );
            }
            String groupClassName = "Router_Group" + "_" + groupName;
            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)
                    .addSuperinterface(ClassName.get(iRouteGroup))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder("com.example.xrouter.routes", typeSpec).build();
            try {
                javaFile.writeTo(filerUtils);
            } catch (IOException e) {
                e.printStackTrace();
            }
            rootMap.put(groupName, groupClassName);
        }
    }

//    public class Router_Root_module1 implements IRouteRoot {
//        @Override
//        public void loadInto(Map<String, Class<? extends IRouteGroup>> routes) {
//            routes.put("hello", Router_Group_hello.class);
//            routes.put("module1", Router_Group_module1.class);
//            routes.put("userservice", Router_Group_userservice.class);
//        }
//    }
    private void generatedRoot(TypeElement iRouteRoot, TypeElement iRouteGroup) {
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(iRouteGroup))
                )
        );
        ParameterSpec parameter = ParameterSpec.builder(parameterizedTypeName, "routes").build();

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("loadInto")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(parameter);

        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            methodBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get("com.example.xrouter.routes", entry.getValue()));
        }

        String className = "Router_Root" + "_" + moduleName;
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(iRouteRoot))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        try {
            JavaFile.builder("com.example.xrouter.routes", typeSpec).build().writeTo(filerUtils);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public class Router_Providers_module1 implements IProviderGroup {
//        @Override
//        public void loadInto(Map<String, RouteMeta> providers) {
//            providers.put("com.example.module1.IUserService", RouteMeta.build(RouteType.PROVIDER, UserServiceImpl.class, "/userservice/userservice"));
//            providers.put("com.example.module1.HelloService", RouteMeta.build(RouteType.PROVIDER, HelloService.class, "/hello/helloservice"));
//        }
//    }
    private void generatedProvider() {
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class)
        );
        ParameterSpec providers = ParameterSpec.builder(parameterizedTypeName, "providers").build();

        MethodSpec.Builder loadIntoMethodOfProviderBuilder = MethodSpec.methodBuilder("loadInto")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(providers);

        for (Map.Entry<String, List<RouteMeta>> entry : groupMap.entrySet()) {
            List<RouteMeta> groupData = entry.getValue();
            for (RouteMeta routeMeta : groupData) {
                switch (routeMeta.getType()) {
                    case PROVIDER:
                        List<? extends TypeMirror> interfaces = ((TypeElement) routeMeta.getElement()).getInterfaces();
                        for (TypeMirror tm : interfaces) {
                            if (typeUtils.isSameType(tm, iProvider)) {
                                loadIntoMethodOfProviderBuilder.addStatement(
                                        "providers.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S))",
                                        (routeMeta.getElement()).toString(),
                                        ClassName.get(RouteMeta.class),
                                        ClassName.get(RouteType.class),
                                        ClassName.get((TypeElement) routeMeta.getElement()),
                                        routeMeta.getPath()
                                );
                            } else if (typeUtils.isSubtype(tm, iProvider)) {
                                loadIntoMethodOfProviderBuilder.addStatement(
                                        "providers.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S))",
                                        tm.toString(),
                                        ClassName.get(RouteMeta.class),
                                        ClassName.get(RouteType.class),
                                        ClassName.get((TypeElement) routeMeta.getElement()),
                                        routeMeta.getPath()
                                );
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

        }
        String providerMapFileName = "Router_Providers_" + moduleName;
        JavaFile javaFile = JavaFile.builder(ROUTE_ROOT_PACKAGE,
                TypeSpec.classBuilder(providerMapFileName)
                        .addSuperinterface(ClassName.get(iProviderGroup))
                        .addModifiers(PUBLIC)
                        .addMethod(loadIntoMethodOfProviderBuilder.build())
                        .build()
        ).build();
        try {
            javaFile.writeTo(filerUtils);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
