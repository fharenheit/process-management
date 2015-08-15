package org.opencloudengine.users.fharenheit.aspectj;


import org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;

public class FlamingoAgent {

	private static Instrumentation s_instrumentation;

	private static ClassFileTransformer s_transformer = new ClassPreProcessorAgentAdapter();

	public static void premain(String options, Instrumentation instrumentation) {
		/* Handle duplicate agents */
		if (s_instrumentation != null) {
			return;
		}
		s_instrumentation = instrumentation;
		s_instrumentation.addTransformer(s_transformer);
	}

	public static Instrumentation getInstrumentation() {
		if (s_instrumentation == null) {
			throw new UnsupportedOperationException("Java 5 was not started with preMain -javaagent for AspectJ");
		}
		return s_instrumentation;
	}

}
