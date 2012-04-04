package bgu.dcr.az.cpu.server.upload;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import bgu.dcr.az.cpu.server.utils.Files;
import bgu.dcr.az.cpu.server.utils.Files.FileSystemException;

public class UploadManager extends UploadAction {

	@Override
	public String executeAction(HttpServletRequest request,
			List<FileItem> sessionFiles) throws UploadActionException {
		try {
			if (!createFolders())
				return ReturnMessage.M_504.getMessage();
			if (sessionFiles.size() > 1)
				return ReturnMessage.M_501.getMessage();
			return validateAndStoreFile(sessionFiles.get(0)).getMessage();
		} finally {
			removeSessionFileItems(request);
			try {
				Files.delete(new File("azdata/tmp/"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean createFolders() {
		try {
			Files.ensureDirectoryExists("azdata/exp/");
			Files.ensureDirectoryExists("azdata/ext-lib/");
			Files.ensureDirectoryExists("azdata/src/");
			Files.ensureDirectoryExists("azdata/tmp/");
			return true;
		} catch (FileSystemException e) {
			return false;
		}
	}

	private ReturnMessage validateAndStoreFile(FileItem f) {
		try {
			String ext = getExtantion(f);
			if ("xml".equals(ext)) {
				f.write(new File("azdata/exp/"+f.getName()));
			} else if ("zip".equals(ext)) {
				File zip=new File("azdata/tmp/"+f.getName());
				File folder=new File("azdata/tmp/ext/");
				f.write(zip);
				Files.unzip(zip ,folder);
				String [] list=folder.list();
				if(list.length!=2){
					return ReturnMessage.M_503;
				}
				for(String str: list){
					if(! ("src".equals(str) || "lib".equals(str))){
						return ReturnMessage.M_503;
					}
				}
				if(!(testFailes(new File("azdata/tmp/ext/src"),"java")&&testFailes(new File("azdata/tmp/ext/lib"),"jar") )){
					return ReturnMessage.M_503;
				}
				Files.copyFolder(new File("azdata/tmp/ext/lib/"), new File("azdata/ext-lib/"));
				Files.copyFolder(new File("azdata/tmp/ext/src/"), new File("azdata/src/"));
				return ReturnMessage.M_200;
			} else {
				return ReturnMessage.M_502;
			}
			return ReturnMessage.M_200;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Files.delete(new File("azdata/ext-lib/"));
				Files.delete(new File("azdata/src/"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return ReturnMessage.M_504;
		}
	}

	private boolean testFailes(File file,String ext){
		if(file.isDirectory()){
			for(File f: file.listFiles()){
				if(!testFailes(f,ext)){
					return false;
				}
			}
			return true;
		}
		else{
			return (ext.equals(getExtantion(file)));
		}
	}
	
	private String getExtantion(FileItem f) {
		String[] name = f.getName().split("\\.");
		if (name.length <= 1) {
			return "";
		}
		return name[name.length - 1];
	}
	
	private String getExtantion(File f) {
		String[] name = f.getName().split("\\.");
		if (name.length <= 1) {
			return "";
		}
		return name[name.length - 1];
	}

	enum ReturnMessage {
		M_200("200 OK"), 
		M_501("501 more then one file was selected"), 
		M_502("502  invalide file type"), 
		M_503("503 invalid folder hierarchy"), 
		M_504("504 failed to store the files");

		private String message;

		private ReturnMessage(String mes) {
			this.message = mes;
		}

		public String getMessage() {
			return message;
		}
	}

}
