package agt0.dev.launch;

import static agt0.dev.util.JavaUtils.println;
import static agt0.dev.util.SWTUtils.errbox;
import static agt0.dev.util.SWTUtils.openDialog;

import java.io.File;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import agt0.dev.FrameworkUpdateUnit;
import agt0.dev.SharedDataUnit;
import agt0.dev.project.AgentZeroProject;
import agt0.dev.util.EclipseUtils;

public class Launcher extends AbstractJavaLaunchConfigurationDelegate {

	public static final String LAUNCH_CONFIGURATION_TYPE_ID = "agt0.dev.launch.launcher";
	public static final String PROBLEM_ATTRIBUTE = "PROBLEM";


	@Override
	public void launch(final ILaunchConfiguration configuration, final String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		if (!SharedDataUnit.UNIT.isLibreryExists()){
			FrameworkUpdateUnit.UNIT.testForFirstUpdate();
			launch.terminate();
			return;
		}
		
		IJavaProject project = getJavaProject(configuration);
		if (project == null){
			project = EclipseUtils.getActiveJavaProject();
		}
		
		AgentZeroProject azp = new AgentZeroProject(project);
		
		if (project == null) {
			System.out.println("The Launcher was called with no project!!!");
		} else {

			IVMInstall vm = verifyVMInstall(configuration);
			IVMRunner runner = vm.getVMRunner(mode);
			
			final String localPath = project.getProject().getLocation().toOSString();	
			
			//String classPath =  project.getProject().getFolder("lib").getLocation()
			//				.toString().replace("/", "\\") + "\\*";
			
			String binClassPath = azp.getBinDirectory().getAbsolutePath();//project.getProject().getFolder("bin").toString();
			
			//classPath = "\"" + classPath + "\"";
			//binClassPath = "\"" + binClassPath + "\"";
			
			//final String agentClass = resolveAgentClass(project);

			//println("agent class is: " + agentClass);
			
			//if (agentClass == null) {
			//	println("user not select agent!");
			//	return; // the execution was canceled by the user..
			//}
			
			File[] libFiles = SharedDataUnit.UNIT.getAllJarsInLib();
			String[] classPath = new String[libFiles.length + 1];
			for (int i=0; i<libFiles.length; i++){
				classPath[i] = libFiles[i].getAbsolutePath();
			}
			
//			String tempJfxInstallation = "/Program Files/Oracle/JavaFX 2.1 Runtime";
//			File fxInstall = new File(tempJfxInstallation);
			
//			for ()
			
			classPath[libFiles.length-1] = binClassPath; 
//			LinkedList<String> cpath2 = new LinkedList<String>();
			
			
			VMRunnerConfiguration runConfig = new VMRunnerConfiguration(
					"bgu.dcr.az.dev.Agent0Tester", classPath);
			
			println("localpath is " + localPath);
			
			LinkedList<String> programArgs = new LinkedList<String>(){{
				add("-f"); 	add("test.xml");
				//add("--es"); //TODO: remove..
				//add("--cp"); add("bin");
				//add("-a"); add(agentClass);
				//if (configuration.getAttribute(PROBLEM_ATTRIBUTE, "").isEmpty()){
					add("--sfp"); add("fails");
				//}else {
				//	add("--prob"); add(configuration.getAttribute(PROBLEM_ATTRIBUTE, ""));
				//}
				//add("--gui");
				add("--emode"); add(mode);
			}};
			
			runConfig.setProgramArguments(programArgs.toArray(new String[0]));

			runConfig.setVMArguments(new String[] { "-Xmx1024m",
					"-XX:MaxPermSize=128m" });

//			File workingDir = verifyWorkingDirectory(configuration);
//			String workingDirName = null;
//			if (workingDir != null) {
//				workingDirName = workingDir.getAbsolutePath();
//			}

			runConfig.setWorkingDirectory(localPath);

			// Bootpath
			String[] bootpath = getBootpath(configuration);
			runConfig.setBootClassPath(bootpath);

			// this will attach the source to the execution.
			setDefaultSourceLocator(launch, configuration);

			runner.run(runConfig, launch, monitor);
			System.out.println("launcer done");
			
		}
	}

	private void initializeView() {
		//showView(AlgorithmExaminer.ID);
	}

	private String resolveAgentClass(IJavaProject project) {
		AgentSelectionModel model = new AgentSelectionModel();
		model.loadAgents(project);
		
		int size = model.getAgents().size();
		if (size == 0){
			errbox("cannot find agent to execute..");
			return null;
		}else if (size == 1){
			return model.agent2class(model.getAgents().get(0));
		} else if (openDialog(AgentSelectionDialog.class, model)){
			return model.getSelectedAgentClassName();
		} else {
			return null;
		}
	}
//
//	public static interface IEventListener {
//		void onEvent(Event e);
//	}

//	public static enum EventManager {
//		INSTANCE;
//
//		public static Event RESET_EVENT = new Event("launcher-reset",
//				new HashMap<String, String>());
//
//		PrefixMap<List<IEventListener>> prefixMap;
//		HashSet<IEventListener> all;
//
//		private EventManager() {
//			prefixMap = new PrefixMap<List<IEventListener>>();
//			all = new HashSet<Launcher.IEventListener>();
//		}
//
//		public void register(String prefix, Launcher.IEventListener listener) {
//			List<IEventListener> pfl = prefixMap.getExact(prefix);
//			if (pfl == null) {
//				pfl = new LinkedList<IEventListener>();
//				prefixMap.put(prefix, pfl);
//			}
//
//			pfl.add(listener);
//			all.add(listener);
//		}
//		
//		public void unRegister(final Launcher.IEventListener listener){
//			prefixMap.map(new Fn1<List<IEventListener>, List<IEventListener>>() {
//				
//				@Override
//				public List<IEventListener> invoke(List<IEventListener> arg) {
//					arg.remove(listener);
//					return arg;
//				}
//			});
//		}
//
//		public void fireReset() {
//			for (IEventListener a : all)
//				a.onEvent(RESET_EVENT);
//		}
//		
//		public void fire(Event e){
//			List<List<IEventListener>> lis = prefixMap.get(e.getName());
//			for (List<IEventListener> l : lis){
//				for (IEventListener el : l){
//					el.onEvent(e);
//				}
//			}
//		}
//
//	}
}
