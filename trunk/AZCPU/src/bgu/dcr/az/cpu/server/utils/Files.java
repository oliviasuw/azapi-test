package bgu.dcr.az.cpu.server.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class Files {
	
	/**
	 * will attempt to create the given directory if it is not exists,
	 * if the path given points to a file will throw FileSystemException
	 * @param dir
	 */
	public static void ensureDirectoryExists(String dir){
		File f = new File(dir);
		if (f.exists() && !f.isDirectory()) throw new FileSystemException("using file as directory - '" + dir + "'");
		if (!f.exists()) f.mkdirs();
	}
	
	/**
	 * the method unzips the chosen file to the dest folder
	 * @param zipfile
	 * @param directory
	 * @throws IOException
	 */
	public static void unzip(File zipfile, File directory) throws IOException {
	    ZipFile zfile = new ZipFile(zipfile);
	    Enumeration<? extends ZipEntry> entries = zfile.entries();
	    while (entries.hasMoreElements()) {
	      ZipEntry entry = entries.nextElement();
	      File file = new File(directory, entry.getName());
	      if (entry.isDirectory()) {
	        file.mkdirs();
	      } else {
	        file.getParentFile().mkdirs();
	        InputStream in = zfile.getInputStream(entry);
	        try {
	          copy(in, file);
	        } finally {
	          in.close();
	        }
	      }
	    }
	    zfile.close();
	  }


	  private static void copy(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    while (true) {
	      int readCount = in.read(buffer);
	      if (readCount < 0) {
	        break;
	      }
	      out.write(buffer, 0, readCount);
	    }
	  }


	  private static void copy(InputStream in, File file) throws IOException {
	    OutputStream out = new FileOutputStream(file);
	    try {
	      copy(in, out);
	    } finally {
	      out.close();
	    }
	  }

    
	/**
	 * the method deletes all the files undr this pathe
	 * @param file the file/folder path
	 * @throws IOException
	 */
    public static void delete(File file) throws IOException{
    	if(file.isDirectory()){	 
    		if(file.list().length==0){
    		   file.delete(); 
    		}else{
        	   String files[] = file.list();
        	   for (String temp : files) {
        	      File fileDelete = new File(file, temp);
        	     delete(fileDelete);
        	   }
        	   if(file.list().length==0){
           			file.delete();
        	   }
    		}
    	}else{
    			file.delete();
    	}
    }
    /**
     * the method copies the folder contents from src to dest if dest doesn't exist it is created
     * @param src from where to copy
     * @param dest where to copy
     * @throws IOException
     */
    public static void copyFolder(File src, File dest)
        	throws IOException{
        	if(src.isDirectory()){
        		if(!dest.exists()){
        		   dest.mkdir();
        		}
        		String files[] = src.list();
        		for (String file : files) {
        		   File srcFile = new File(src, file);
        		   File destFile = new File(dest, file);
        		   copyFolder(srcFile,destFile);
        		}
        	}else{
        		InputStream in = new FileInputStream(src);
        	        OutputStream out = new FileOutputStream(dest); 
        	        byte[] buffer = new byte[1024];
        	        int length; 
        	        while ((length = in.read(buffer)) > 0){
        	    	   out.write(buffer, 0, length);
        	        }
        	        in.close();
        	        out.close();
        	}
    }
    
    
	public static class FileSystemException extends RuntimeException{

		public FileSystemException() {
			super();
			// TODO Auto-generated constructor stub
		}

		public FileSystemException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
			// TODO Auto-generated constructor stub
		}

		public FileSystemException(String message, Throwable cause) {
			super(message, cause);
			// TODO Auto-generated constructor stub
		}

		public FileSystemException(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}

		public FileSystemException(Throwable cause) {
			super(cause);
			// TODO Auto-generated constructor stub
		}
		
	}
}
