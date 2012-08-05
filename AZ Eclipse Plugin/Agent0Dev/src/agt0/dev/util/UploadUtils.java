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
			FileUtils.copy(path + filePath, srcFolder.getAbsolutePath()
					+ file.getPath().substring(5));
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
		zipFolder(srcFolder.getAbsolutePath(),libFolder.getAbsolutePath(),zipFile.getAbsolutePath());
		FileUtils.delete(libFolder);
		FileUtils.delete(srcFolder);
		FileUtils.delete(zipFolder);
		return zipFile;

	}

	static public void zipFolder(String srcFolder,String libFolder , String destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;

        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);

        addFolderToZip("", srcFolder, zip);
        addFolderToZip("", libFolder, zip);
        zip.flush();
        zip.close();
    }

    static private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
            throws Exception {

        File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zip);
        } else {
            byte[] buf = new byte[1024];
            int len;
            //srcFile = srcFile.substring(5);
            System.out.println(path + " " + srcFile);
            FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
        }
    }

    static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
            throws Exception {
        File folder = new File(srcFolder);

        for (String fileName : folder.list()) {
            System.out.println(fileName);
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
            }
        }
    }

	
	
	
//	private static void writeTozip(File srcFolder, ZipOutputStream out)
//			throws FileNotFoundException, IOException {
//		String path = srcFolder.getName() + "\\";
//		byte[] tmpBuf = new byte[1024];
//		for (File file : srcFolder.listFiles()) {
//			FileInputStream in = new FileInputStream(file.getAbsolutePath());
//			System.out.println(" Adding: " + path + file.getName());
//			out.putNextEntry(new ZipEntry(path + file.getName()));
//			int len;
//			while ((len = in.read(tmpBuf)) > 0) {
//				out.write(tmpBuf, 0, len);
//			}
//			out.closeEntry();
//			in.close();
//		}
//	}
//
//	public static void zipDir(File dirPath, ZipOutputStream zos) {
//		try {
//			// create a ZipOutputStream to zip the data to
//			
//			// assuming that there is a directory named inFolder (If there
//			// isn't create one) in the same directory as the one the code
//			// runs from,
//			// call the zipDir method
//			zipDir(dirPath,"", zos);
//			// close the stream
//			zos.close();
//		} catch (Exception e) {
//			// handle exception
//		}
//		// here is the code for the method
//	}
//
//	public static void zipDir(File dir2zip,String inZip, ZipOutputStream zos) {
//		try {
//			File[] fileList = dir2zip.listFiles();
//			// loop through dirList, and zip the files
//			for (int i = 0; i < fileList.length; i++) {
//				if (fileList[i].isDirectory()) {
//					ZipEntry anEntry = new ZipEntry(inZip+fileList[i].getName());
//					zos.putNextEntry(anEntry);
//					zipDir(fileList[i],inZip+"\\"+fileList[i].getName(), zos);
//					// loop again
//					
//					continue;
//				}
//				// if we reached here, the File object f was not
//				// a directory
//				// create a FileInputStream on top of f
//				FileInputStream fis = new FileInputStream(fileList[i]);
//				// create a new zip entry
//				ZipEntry anEntry = new ZipEntry(inZip+"\\"+fileList[i].getName());
//				// place the zip entry in the ZipOutputStream object
//				zos.putNextEntry(anEntry);
//				// now write the content of the file to the ZipOutputStream
//				byte[] readBuffer = new byte[2156];
//				int bytesIn = 0;
//				while ((bytesIn = fis.read(readBuffer)) != -1) {
//					zos.write(readBuffer, 0, bytesIn);
//				}
//				// close the Stream
//				fis.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

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

	public static void uploadUsingPost(File zip, String user, String password) {
		HttpClient client = null;
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
