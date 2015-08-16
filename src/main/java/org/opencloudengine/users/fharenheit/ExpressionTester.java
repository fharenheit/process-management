package org.opencloudengine.users.fharenheit;

import javax.el.ExpressionFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ExpressionTester {

    public static void main(String[] args) throws NoSuchMethodException {
        ExpressionFactory factory = new de.odysseus.el.ExpressionFactoryImpl();

        de.odysseus.el.util.SimpleContext context = new de.odysseus.el.util.SimpleContext();

        String isEmptyDef = "string:empty=org.opencloudengine.users.fharenheit.StringUtils#isEmpty";
        String[] defs = parseDefinition(isEmptyDef);
        Method method = findMethod(defs[2], defs[3]);

        context.setFunction("string", "isEmpty", method);
        context.setFunction("math", "max", Math.class.getMethod("max", int.class, int.class));

        context.setVariable("foo", factory.createValueExpression("123", String.class));
        context.setVariable("bar", factory.createValueExpression("123", String.class));

        System.out.println(factory.createValueExpression(context, "${math:max(foo,bar)}", String.class).getValue(context));
        System.out.println(factory.createValueExpression(context, "${string:isEmpty(foo)}", Boolean.class).getValue(context));
    }

    public static String[] parseDefinition(String str) throws RuntimeException {
        try {
            str = str.trim();
            if (!str.contains(":")) {
                str = ":" + str;
            }
            String[] parts = str.split(":");
            String prefix = parts[0];
            parts = parts[1].split("=");
            String name = parts[0];
            parts = parts[1].split("#");
            String klass = parts[0];
            String method = parts[1];
            return new String[]{prefix, name, klass, method};
        } catch (Exception ex) {
            throw new RuntimeException("Cannot parsing EL definitions", ex);
        }
    }

    public static Method findMethod(String className, String methodName) throws RuntimeException {
        Method method = null;
        try {
            Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            for (Method m : clazz.getMethods()) {
                if (m.getName().equals(methodName)) {
                    method = m;
                    break;
                }
            }
            if (method == null) {
                // throw new RuntimeException(ErrorCode.E0111, className, methodName);
            }
            if ((method.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != (Modifier.PUBLIC | Modifier.STATIC)) {
                // throw new RuntimeException(ErrorCode.E0112, className, methodName);
            }
        } catch (ClassNotFoundException ex) {
            // throw new RuntimeException(ErrorCode.E0113, className);
        }
        return method;
    }

    public static Object findConstant(String className, String constantName) throws RuntimeException {
        try {
            Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            Field field = clazz.getField(constantName);
            if ((field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != (Modifier.PUBLIC | Modifier.STATIC)) {
                // throw new RuntimeException(ErrorCode.E0114, className, constantName);
            }
            return field.get(null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
