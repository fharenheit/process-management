package org.opencloudengine.users.fharenheit.aspectj;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class JMXEndpoint {
	public static void start(ActorSystemMessages messages) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
	}
}