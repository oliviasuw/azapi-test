package agt0.dev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import agt0.dev.util.EclipseUtils;
import agt0.dev.util.FileUtils;

/**
 * this called the shared unit cause it is shared between projects
 * @author bennyl
 *
 */
public enum SharedDataUnit {
	UNIT;
	
	private static final String AZ_WORKSPACE_PATH = EclipseUtils
			.getWorkspaceDirectory().getAbsolutePath() + "/.az";
	
	private static final String LIBRARY_PATH = AZ_WORKSPACE_PATH + "/lib";
	
	private static final String DOC_PATH = AZ_WORKSPACE_PATH + "/doc";
	
	public static final String JAVADOC_LOCATION_IN_ARCHIVE = "/";
	
	public static final String JFX_LOCATION_STORE_FILE_NAME = "jfx.location";
	
	public static final String JFX_LOCATION_STORE_FILE_PATH = AZ_WORKSPACE_PATH + "/jfx.location";
	
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
	
	public File findJavaFxRuntime(){
		File jfxLocationFile = new File(JFX_LOCATION_STORE_FILE_PATH);
		if (jfxLocationFile.exists()){
			try {
				return new File(FileUtils.unPersistText(jfxLocationFile));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (System.getProperty("os.name").startsWith("Windows")){
			File root = new File("/Program Files/Oracle/");
			for (File f : root.listFiles()){
				if (f.getName().startsWith("JavaFX") && f.getName().endsWith("Runtime")){
					return f;
				}
			}
		}
		
		return null;
	}
	
	public void storeJavaFxRuntimeLocation(File where){
		try {
			FileUtils.persistText(new File(AZ_WORKSPACE_PATH), JFX_LOCATION_STORE_FILE_NAME, where.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
