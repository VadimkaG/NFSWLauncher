package ru.vadimka.nfswlauncher;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;

public class ProcessUtils {
	static {
		String libname = "";
		if (System.getProperty("os.arch").toLowerCase().equalsIgnoreCase("amd64")) {
			libname = "RWACPUx64.dll";
		} else {
			libname = "RWACPUx32.dll";
		}
		try {
			final InputStream in = ProcessUtils.class.getResourceAsStream(libname);
			if (in != null) {
				final File temp = File.createTempFile(libname, "");
				final byte[] buffer = new byte[1024];
				int read = -1;
				final FileOutputStream fos = new FileOutputStream(temp);
				while ((read = in.read(buffer)) != -1) {
					fos.write(buffer, 0, read);
				}
				fos.close();
				in.close();
				System.load(temp.getAbsolutePath());
			} else {
				System.out.println("[RWACProcessUtils] \u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0431\u0438\u0431\u043b\u0438\u043e\u0442\u0435\u043a\u0443");
			}
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private ProcessUtils() {}
	
	public static native void renameTitle(final String title, final long handle);
	
	public static native String check(final long handle);
}