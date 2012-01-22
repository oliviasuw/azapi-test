package agt0.dev;

import java.io.File;
import java.util.List;

import agt0.dev.util.EclipseUtils;

/**
 * this called the shared unit cause it is shared between projects
 * @author bennyl
 *
 */
public enum SharedDataUnit {
	UNIT;
	
	private static final String LIBRARY_PATH = EclipseUtils
			.getWorkspaceDirectory().getAbsolutePath() + "/.az/lib";
	
	private static final String DOC_PATH = EclipseUtils
			.getWorkspaceDirectory().getAbsolutePath() + "/.az/doc";
	
	public static final String JAVADOC_LOCATION_IN_ARCHIVE = "/";
	
	/**
	 * @return list of all the jars that stored in the lib folder or empty list if no such folder
	 */
	public File[] getAllJarsInLib(){
		File lib = new File(LIBRARY_PATH);
		if (lib.exists()){
			return lib.listFiles();
		}
		
		return new File[0];
	}
	
	/**
	 * @return true if the lib folder exists
	 */
	public boolean isLibreryExists(){
		return new File(LIBRARY_PATH).exists();
	}
	
	public File getApiJar(){
		return new File(LIBRARY_PATH + "/AZAPI.jar" );
	}
	
	public File getCoreJar(){
		return new File(LIBRARY_PATH + "/AZCoreImpl.jar" );
	}
	
	public File getApiJavaDocZip(){
		return new File(DOC_PATH + "/javadoc.jar" );
	}
	
}
