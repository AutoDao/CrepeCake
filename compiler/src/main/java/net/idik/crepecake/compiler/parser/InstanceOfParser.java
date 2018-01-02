package net.idik.crepecake.compiler.parser;

import net.idik.crepecake.annotations.InstanceOf;
import net.idik.crepecake.api.InvocationHandler;
import net.idik.crepecake.compiler.Utils;
import net.idik.crepecake.compiler.data.InstanceOfSpec;
import net.idik.crepecake.compiler.data.MethodSpec;
import net.idik.crepecake.compiler.data.VariantSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


/**
 * Created by linshuaibin on 2017/12/28.
 */

public class InstanceOfParser {

    private Types types;
    private Elements elements;
    private Messager messager;

    public InstanceOfParser(Types types, Elements elements, Messager messager) {
        this.types = types;
        this.elements = elements;
        this.messager = messager;
    }

    public InstanceOfSpec parse(TypeElement element) {
        InstanceOf instanceOf = element.getAnnotation(InstanceOf.class);
        String className = Utils.getRawString(instanceOf, "value");
        List<MethodSpec> invocationMethods = new ArrayList<>();
        element.getEnclosedElements().forEach(it -> {
            if (it.getKind() == ElementKind.METHOD && ((ExecutableElement) it).getParameters().get(0).asType().toString().equals(InvocationHandler.class.getTypeName())) {
                invocationMethods.add(parseMethod((ExecutableElement) it));
            }
        });
        return new InstanceOfSpec(className, element.getQualifiedName().toString(), invocationMethods);
    }

    private MethodSpec parseMethod(ExecutableElement element) {
        List<? extends VariableElement> parameterElements = element.getParameters();
        VariantSpec[] parameters = new VariantSpec[parameterElements.size() - 1];
        for (int i = 1; i < parameterElements.size(); i++) {
            parameters[i - 1] = parseVariant(parameterElements.get(i));
        }
        String name = element.getSimpleName().toString();
        String returnType = element.getReturnType().toString();
        return new MethodSpec(name, returnType, parameters);
    }

    private VariantSpec parseVariant(VariableElement element) {
        return new VariantSpec(element.getSimpleName().toString(), element.asType().toString());
    }
}
