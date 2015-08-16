package org.opencloudengine.users.fharenheit.aspectj;

import org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * <pre>MATA-INF/MANIFEST</pre> 파일의 <pre>Premain-Class</pre> 속성으로 설정하여
 * <pre>-javaagent</pre> 설정시 초기홯는 Agent의 Endtry Point.
 */
public class AspectJAgent {

    private static Instrumentation instrumentation;

    private static ClassFileTransformer transformer = new ClassPreProcessorAgentAdapter();

    public static void premain(String options, Instrumentation instrumentation) {
        if (AspectJAgent.instrumentation != null) {
            return;
        }
        AspectJAgent.instrumentation = instrumentation;
        AspectJAgent.instrumentation.addTransformer(transformer);

        System.err.println("INFO: (Enh120375):  premain");
    }

    public static Instrumentation getInstrumentation() {
        if (instrumentation == null) {
            throw new UnsupportedOperationException("Java 5 was not started with preMain -javaagent for AspectJ");
        }
        return instrumentation;
    }
}
