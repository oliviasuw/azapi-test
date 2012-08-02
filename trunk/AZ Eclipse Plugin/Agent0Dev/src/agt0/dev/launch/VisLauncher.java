package agt0.dev.launch;

import static agt0.dev.util.JavaUtils.println;
import static agt0.dev.util.SWTUtils.errbox;
import static agt0.dev.util.SWTUtils.openDialog;
import static agt0.dev.util.SWTUtils.showView;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import agt0.dev.FrameworkUpdateUnit;
import agt0.dev.SharedDataUnit;
import agt0.dev.project.AgentZeroProject;
import agt0.dev.ui.WhereIsJFXDialog;
import agt0.dev.util.EclipseUtils;
import agt0.dev.util.JavaUtils.Fn1;
import agt0.dev.util.ds.PrefixMap;

public class VisLauncher extends AbstractJavaLaunchConfigurationDelegate {

	public static final String LAUNCH_CONFIGURATION_TYPE_ID = "agt0.dev.launch.vlauncher";


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
						
			String binClassPath = azp.getBinDirectory().getAbsolutePath();//project.getProject().getFolder("bin").toString();
						
			File[] libFiles = SharedDataUnit.UNIT.getAllJarsInLib();
			String[] classPath = new String[libFiles.length + 2];
			for (int i=0; i<libFiles.length; i++){
				classPath[i] = libFiles[i].getAbsolutePath();
			}
			
			classPath[libFiles.length] = binClassPath; 
			
			File jfxr = SharedDataUnit.UNIT.findJavaFxRuntime();
			if (jfxr != null){
				classPath[libFiles.length+1] = jfxr.getAbsolutePath() + "/lib/jfxrt.jar";
			}else {
				WhereIsJFXDialog dialog = EclipseUtils.openDialog(WhereIsJFXDialog.class);
				jfxr = SharedDataUnit.UNIT.findJavaFxRuntime();
				if (jfxr != null){
					classPath[libFiles.length+1] = jfxr.getAbsolutePath() + "/lib/jfxrt.jar";
				}	
			}
			
			VMRunnerConfiguration runConfig = new VMRunnerConfiguration(
					"bgu.dcr.az.vdev.app.VisViewApp", classPath);
			
			println("localpath is " + localPath);
			
			LinkedList<String> programArgs = new LinkedList<String>(){{
				add("--efile=test.xml");
			}};
			
			runConfig.setProgramArguments(programArgs.toArray(new String[0]));

			runConfig.setVMArguments(new String[] { "-Xmx1024m",
					"-XX:MaxPermSize=128m" });

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

}
