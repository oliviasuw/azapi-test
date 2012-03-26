package bgu.dcr.az.cpu.server.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import bgu.dcr.az.cpu.server.api.CPU;
import bgu.dcr.az.cpu.server.api.ExperimentManager;
import bgu.dcr.az.cpu.server.api.UserState;
import bgu.dcr.az.cpu.server.utils.Ants;
import bgu.dcr.az.cpu.server.utils.Files;
import bgu.dcr.az.cpu.server.utils.Processes;
import bgu.dcr.az.cpu.shared.AlgorithmData;

public class CPUServer implements CPU{

	public static String EXECUTION_DATA_PATH = "execution-data";
	public static String EXPERIMENTS_PATH = "experiments";
	
	public static final String DATA_FOLDER_PATH = "azdata";
	public static final String LIB_FOLDER_PATH = DATA_FOLDER_PATH + "/lib";
	public static final String AZ_BUILD_XML_PATH = DATA_FOLDER_PATH + "/azbuild.xml";
	public static final String AZ_BUILD_PATH = DATA_FOLDER_PATH + "/build";
		
	
	private static CPUServer instance = null;
	private UserState currentUserState;
	private ExperimentManager expManager;
	
	public static CPUServer get(){
		if (instance == null) instance = new CPUServer();
		return instance;
	}
	
	public CPUServer() {
		Files.ensureDirectoryExists(EXECUTION_DATA_PATH);
		Files.ensureDirectoryExists(EXPERIMENTS_PATH);
	}
	
	@Override
	public UserState getUserState() {
		return currentUserState;
	}

	@Override
	public ExperimentManager getExperimentManager() {
		return expManager;
	}
	

	/**
	 * @return all the available algorithms in the loaded experiment pack.
	 */
	public List<AlgorithmData> listAlgorithms(){
		Processes.execAndWait("bc.utils.AlgorithmsScanner", CPUServer.LIB_FOLDER_PATH, AZ_BUILD_PATH, "algorithms.csv");
		LinkedList<AlgorithmData> ret = new LinkedList<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File("algorithms.csv")));
			String line;
			
			while ((line = br.readLine()) != null){
				String[] data = line.split(",");
				if (data.length == 2){
					ret.add(new AlgorithmData(data[0], data[1]));
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
	
	public static void main(String[] args){
		Ants.build(AZ_BUILD_XML_PATH);
		System.out.println("" + Objects.toString(CPUServer.get().listAlgorithms()));
	}

}
