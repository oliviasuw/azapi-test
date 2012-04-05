package bgu.dcr.az.cpu.server.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import bgu.dcr.az.cpu.client.CPUClient;

public class Processes {

	private static String createClassPathString(String libPath) {
		try {
			StringBuilder sb = new StringBuilder();
			File libsFolder = new File(libPath);

			if (libsFolder.isDirectory()) {

				for (File l : libsFolder.listFiles()) {
					sb.append(l.getAbsolutePath()).append(";");
				}
				File jarFile = new File(CPUClient.class.getProtectionDomain()
						.getCodeSource().getLocation().toURI());
				sb.append(jarFile.getAbsolutePath()).append(";");
				return sb.toString();
			}
			return "";
		} catch (URISyntaxException ex) {
			Logger.getLogger(CPUClient.class.getName()).log(Level.SEVERE, null,
					ex);
		}

		return "";// somthing bad happened
	}

	/**
	 * exec other java process and wait for it to end
	 * 
	 * @param mainClass
	 *            the class to start
	 * @param libPath
	 *            the path where all the librery jars are sit
	 * @param classPath
	 *            additional class path - list that is seperated by ';'
	 * @param args
	 *            the arguments to send to the new program
	 * @return the exit code of the child process
	 */
	public static int execAndWait(String mainClass, String libPath,
			String classPath, String... args) {
		String classPathString = createClassPathString(libPath) + classPath
				+ ";";
		String[] command = AzArrays.concatanate(new String[] { "java", "-cp",
				classPathString, mainClass }, args);

		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		Process p = null;
		try {
			p = pb.start();
			InputStream out = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(out));
			String line;

			while ((line = br.readLine()) != null) {
				System.out.println(">> " + line);
			}
		} catch (IOException e) {

		}

		if (p != null) {
			return p.exitValue();
		}

		return -1;
	}
}
