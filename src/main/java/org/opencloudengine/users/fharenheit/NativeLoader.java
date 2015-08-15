package org.opencloudengine.users.fharenheit;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class NativeLoader {

	private static final Log LOG = LogFactory.getLog(NativeLoader.class);

	public static void loadSigarNative() {
		try {
			if (SystemUtils.IS_OS_LINUX) {
				NativeUtils.loadLibraryFromJar("/native/libsigar-amd64-linux.so");
			} else if (SystemUtils.IS_OS_MAC_OSX) {
				NativeUtils.loadLibraryFromJar("/native/libsigar-universal64-macosx.dylib");
			}
		} catch (IOException e) {
			LOG.warn("Cannot load sigar native library : " + e.getMessage());
			e.printStackTrace(); // This is probably not the best way to handle exception :-)
		}
	}
}