package agt0.dev.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.xml.sax.InputSource;

import agt0.dev.SharedDataUnit;
import agt0.dev.project.AgentZeroProject;

public class UploadUtils {

	public static File createZip(List<File> src, List<File> lib)
			throws Exception {
		File tempFolder = SharedDataUnit.UNIT.getTempFolder();
		String path = AgentZeroProject.activeProject().getJavaProject()
				.getJavaModel().getWorkspace().getRoot().getLocation()
				.toOSString()
				+ AgentZeroProject.activeProject().getJavaProject().getPath()
						.toOSString();
		File zipFolder = new File(tempFolder, "zip");
		if (!zipFolder.exists())
			zipFolder.mkdir();
		File srcFolder = new File(zipFolder, "src");
		if (!srcFolder.exists())
			srcFolder.mkdir();
		for (File file : src) {
			String filePath = file.getPath().substring(1);
			FileUtils.copy(path + filePath, srcFolder.getAbsolutePath() + "\\"
					+ file.getName());
		}
		File libFolder = new File(zipFolder, "lib");
		if (!libFolder.exists())
			libFolder.mkdir();
		for (File file : lib) {
			FileUtils.copy(file.getAbsolutePath(), libFolder.getAbsolutePath()
					+ "\\" + file.getName());
		}
		File zipFile = new File(tempFolder, "export.zip");
		if (!libFolder.exists())
			libFolder.mkdir();

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		byte[] tmpBuf = new byte[1024];
		out.putNextEntry(new ZipEntry("lib\\"));
		out.putNextEntry(new ZipEntry("src\\"));
		out.closeEntry();
		writeTozip(libFolder, out);
		writeTozip(srcFolder, out);
		out.close();
		FileUtils.delete(libFolder);
		FileUtils.delete(srcFolder);
		FileUtils.delete(zipFolder);
		return zipFile;

	}

	private static void writeTozip(File srcFolder, ZipOutputStream out)
			throws FileNotFoundException, IOException {
		String path = srcFolder.getName() + "\\";
		byte[] tmpBuf = new byte[1024];
		for (File file : srcFolder.listFiles()) {
			FileInputStream in = new FileInputStream(file.getAbsolutePath());
			System.out.println(" Adding: " + path + file.getName());
			out.putNextEntry(new ZipEntry(path + file.getName()));
			int len;
			while ((len = in.read(tmpBuf)) > 0) {
				out.write(tmpBuf, 0, len);
			}
			out.closeEntry();
			in.close();
		}
	}

	public static List<File> coolectJars(IJavaProject project)
			throws JavaModelException {

		IPackageFragmentRoot[] elements = project.getAllPackageFragmentRoots();
		List<File> list = new ArrayList<File>();
		for (IPackageFragmentRoot pfr : elements) {
			IClasspathEntry ce = pfr.getResolvedClasspathEntry();
			if (ce.getEntryKind() == IClasspathEntry.CPE_LIBRARY
					&& filter(ce.getPath().toString())) {
				list.add(new File(project.getJavaModel().getWorkspace()
						.getRoot().getLocation()
						+ ce.getPath().toString()));

			}
		}
		return list;
	}

	private static boolean filter(String str) {
		String val = str.toLowerCase();
		if (val.contains("\\.az\\lib\\") || val.contains("/.az/lib/")
				|| val.contains("java\\jre") || val.contains("java/jre")
				|| val.contains("\\java\\jdk") || val.contains("/java/jdk")) {
			return false;
		}
		return true;
	}

	public static void uploadUsingPost(File zip,String user,String password) {
		HttpClient client=null;
		try {
			String url = "http://10.0.0.138:8084/Az/file-upload-servlet";
			client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpPost post = new HttpPost(url);
			post.addHeader("user", user);
			post.addHeader("password", password);
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			entity.addPart("attachment_field", new FileBody(zip,
					"application/octet-stream"));
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			if (200 != response.getStatusLine().getStatusCode()) {
				EclipseUtils.showError("the server returned :"
						+ inputToString(response.getEntity().getContent()));
			}
			String responseString = EntityUtils.toString(response.getEntity(),
					"UTF-8");
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
	}

	public static String inputToString(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader bf = new BufferedReader(new InputStreamReader(in));
		String strLine;
		while ((strLine = bf.readLine()) != null) {
			sb.append(strLine);
		}
		return sb.toString();
	}
}
