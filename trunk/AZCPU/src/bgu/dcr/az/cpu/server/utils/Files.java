package bgu.dcr.az.cpu.server.utils;

import java.io.File;

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
