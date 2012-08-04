
package agt0.dev.project;

import static agt0.dev.util.JavaUtils.assoc;
import static agt0.dev.util.JavaUtils.concat;
import static agt0.dev.util.PlatformUtils.resource;
import static agt0.dev.util.SourceUtils.ast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import bgu.dcr.az.ecoa.rmodel.ScannedCodeUnit;

import agt0.dev.Global;
import agt0.dev.SharedDataUnit;
import agt0.dev.util.EclipseUtils;
import agt0.dev.util.FileUtils;
import agt0.dev.util.JavaUtils;
import agt0.dev.util.SourceUtils;
import agt0.dev.util.SourceUtils.TypeVisitor;
import agt0.dev.util.ds.TemplateInputStream;

public class AgentZeroProject {
	public static final String NATURE_ID = "agent0dev.project.Agent0Nature";

	private IJavaProject project;

	public AgentZeroProject(IJavaProject jprj) {
		this.project = jprj;
	}

	/**
	 * @return the java project that this project wraps
	 */
	public IJavaProject getJavaProject() {
		return project;
	}

	/**
	 * @return this project root directory see tutorial
	 *         http://help.eclipse.org/indigo
	 *         /index.jsp?topic=%2Forg.eclipse.platform
	 *         .doc.isv%2Fguide%2FresInt_filesystem.htm
	 */
	public File getRootDirectory() {
		return new File(EclipseUtils.getWorkspaceDirectory().getAbsolutePath()
				+ project.getProject().getFullPath().toFile().getPath());
	}

	/**
	 * @return the src directory of this project
	 */
	public File getSrcDirectory() {
		return new File(getRootDirectory().getAbsolutePath() + "/src");
	}

	/**
	 * @return the test.xml file for this project
	 */
	public File getTestXmlFile() {
		return new File(getRootDirectory().getAbsolutePath() + "/test.xml");
	}

	
	/**
	 * @return the directory where all the implemented agents are written
	 */
	public File getAgentsSourceDirectory() {
		return new File(getSrcDirectory().getAbsolutePath() + "/ext/sim/agents");
	}

	/**
	 * @return the directory where all the implemented tools are written
	 */
	public File getToolsSourceDirectory() {
		return new File(getSrcDirectory().getAbsolutePath() + "/ext/sim/tools");
	}
	
	/**
	 * @return the directory where all the implemented tools are written
	 */
	public File getModulesSourceDirectory() {
		return new File(getSrcDirectory().getAbsolutePath() + "/ext/sim/modules");
	}

	/**
	 * @return the directory where all the compiled classes are stored
	 */
	public File getBinDirectory() {
		return new File(getRootDirectory().getAbsolutePath() + "/bin");
	}

	/**
	 * @return the active Agent Zero Project - or null if such project not
	 *         exists, not opened, whats opened is not agent zero project, etc.
	 */
	public static AgentZeroProject activeProject() {
		IJavaProject jprj = getActiveJavaProject();
		try {
			if (jprj == null
					|| !jprj.getProject().hasNature(AgentZeroProject.NATURE_ID)) {
				return null;
			} else {
				return new AgentZeroProject(jprj);
			}
		} catch (CoreException e) {
			return null; // project closed or somthing...
		}
	}

	/**
	 * @return the active java project - there can be no active project or the
	 *         active project is not a java one - in that case we will return
	 *         null see tutorial:
	 *         http://www.stateofflow.com/journal/66/creating-
	 *         java-projects-programmatically
	 *         http://help.eclipse.org/indigo/index
	 *         .jsp?topic=%2Forg.eclipse.jdt.
	 *         doc.isv%2Freference%2Fapi%2Forg%2Feclipse
	 *         %2Fjdt%2Fcore%2FJavaCore.html
	 */
	private static IJavaProject getActiveJavaProject() {
		IWorkbenchPage page = getActivePage();
		IResource res = extractResourceFromSelection(page.getSelection());
		if (res == null) { // try getting the resource from the active editor
			IEditorPart editor = page.getActiveEditor();
			if (editor == null)
				return null;
			res = extractResourceFromEditor(editor);
			if (res == null)
				return null;
		}
		IProject prj = res.getProject();
		if (prj == null)
			return null;
		try {
			if (!prj.hasNature(JavaCore.NATURE_ID)) { // this is not a java
														// project
				return null;
			}
		} catch (CoreException e) {
			return null; // the project probably not opened
		}
		return JavaCore.create(prj);
	}

	/**
	 * @return the active page - the page that the user is currently views if no
	 *         such page - return null from the page you can get the selection/
	 *         editor see tutorial at
	 *         http://wiki.eclipse.org/FAQ_How_do_I_access_the_active_project%3F
	 */
	private static IWorkbenchPage getActivePage() {
		IWorkbench iworkbench = PlatformUI.getWorkbench();
		if (iworkbench == null)
			return null;
		IWorkbenchWindow iworkbenchwindow = iworkbench
				.getActiveWorkbenchWindow();
		if (iworkbenchwindow == null)
			return null;
		return iworkbenchwindow.getActivePage();
	}

	/**
	 * extract resource from an editor such editor can be received from the
	 * active page see tutorial:
	 * http://wiki.eclipse.org/FAQ_How_do_I_access_the_active_project%3F
	 * 
	 * @param editor
	 * @return
	 */
	private static IResource extractResourceFromEditor(IEditorPart editor) {
		IEditorInput input = editor.getEditorInput();
		if (!(input instanceof IFileEditorInput))
			return null;
		return ((IFileEditorInput) input).getFile();
	}

	/**
	 * @param sel
	 *            the selection which you can get from the active page
	 * @return resource represented by the current selection the current
	 *         selection can be lots of things so if it not represents a
	 *         research the function will return null see tutorial at
	 *         http://wiki.eclipse.org/FAQ_How_do_I_access_the_active_project%3F
	 */
	private static IResource extractResourceFromSelection(ISelection sel) {
		if (sel == null)
			return null;
		if (!(sel instanceof IStructuredSelection))
			return null;
		IStructuredSelection ss = (IStructuredSelection) sel;
		Object element = ss.getFirstElement();
		if (element instanceof IResource)
			return (IResource) element;
		if (!(element instanceof IAdaptable))
			return null;
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IResource.class);
		return (IResource) adapter;
	}

	/**
	 * create the default file structured that the project have - the create project 
	 * uses this function when it creating the project
	 * @param defaultAlgorithmName
	 * @throws IOException
	 */
	private void createDefaultProjectStructure(String defaultAlgorithmName) throws IOException {
		this.getAgentsSourceDirectory().mkdirs();
		this.getToolsSourceDirectory().mkdirs();
		this.getModulesSourceDirectory().mkdirs();
		
		String algoName = SourceUtils.camelCase(defaultAlgorithmName); // uc(algoName.charAt(0)) +
		
		Map<String, String> tkeys = assoc(new HashMap<String, String>(),
				"ALGORITHM_NAME", algoName);

		//BUILD TEST.XML
		FileUtils.dump(new TemplateInputStream(tkeys,
				resource("templates/TESTING_CONFIGURATION_TEMPLATE")), getTestXmlFile());

		//BUILD FIRST AGENT
		FileUtils.dump(new TemplateInputStream(tkeys,
				resource("templates/NEW_AGENT_TEMPLATE.java")), new File(getAgentsSourceDirectory().getAbsolutePath() + "/" + algoName + "Agent.java"));

	}
	
	
	/**
	 * will try to create a new agent for the given algorithm name
	 * if success it will return true and open the editor on the created class - if openInEditor is true
	 * @param algorithmName
	 * @param openInEditor
	 * @return
	 */
	public boolean createNewAgent(String algorithmName, boolean openInEditor){
		String ccase = SourceUtils.camelCase(algorithmName);
		try {
			File file = new File(getAgentsSourceDirectory().getAbsolutePath() + "/" + ccase + ".java");
			createFileFromTemplate(file, "NEW_AGENT_TEMPLATE.java" , "ALGORITHM_NAME", ccase);
			refreshInEclipse();
			if (openInEditor) EclipseUtils.openEditorFor(file);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	
	/**
	 * create a new problem generator with the given name
	 * @param name
	 * @return
	 */
	public boolean createNewProblemGenerator(String name, boolean openInEditor){
		return createNewModule("NEW_PROBLEM_GEN_TEMPLATE.java", name, openInEditor);
	}
	
	/**
	 * create a new message delayer with the given name
	 * @param name
	 * @return
	 */
	public boolean createNewMessageDelayer(String name, boolean openInEditor){
		return createNewModule("NEW_MESSAGE_DELAYER_TEMPLATE.java", name, openInEditor);
	}

	
	/**
	 * create a new statistic collector with the given name
	 * @param name
	 * @return
	 */
	public boolean createNewStatisticCollector(String name, boolean openInEditor){
		return createNewModule("NEW_STATISTIC_COLLECTOR_TEMPLATE.java", name, openInEditor);
	}
	
	/**
	 * create a new correctness tester with the given name
	 * @param name
	 * @return
	 */
	public boolean createNewCorrectnessTester(String name, boolean openInEditor){
		return createNewModule("NEW_CORRECTNESS_TESTER_TEMPLATE.java", name, openInEditor);
	}
	
	/**
	 * will try to create a new module for the given algorithm name
	 * if success it will return true and open the editor on the created class - if openInEditor is true
	 * @param moduleTemplate the module template to create
	 * @param moduleName
	 * @param openInEditor
	 * @return
	 */
	private boolean createNewModule(String moduleTemplate, String moduleName, boolean openInEditor, String... moreTemplateParams){
		String ccase = SourceUtils.camelCase(moduleName);
		try {
			File file = new File(getModulesSourceDirectory().getAbsolutePath() + "/" + ccase + ".java");
			Map<String, String> params = buildParamMap(moreTemplateParams);
			params.put("MODULE_NAME", moduleName);
			params.put("MODULE_NAME_CC", ccase);
			createFileFromTemplate(file, moduleTemplate , params);
			refreshInEclipse();
			if (openInEditor) EclipseUtils.openEditorFor(file);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	
	/**
	 * create a new file in the project and filling it with the given template data
	 * the new file is not synchronized with the workspace and you should refresh the project in order to see it...
	 * @param f
	 * @param templateName
	 * @param params
	 * @throws IOException
	 */
	private static void createFileFromTemplate(File f, String templateName, String... params) throws IOException{
		Map<String, String> tkeys = buildParamMap(params);
		createFileFromTemplate(f, templateName, tkeys);
	}

	/**
	 * create a new file in the project and filling it with the given template data
	 * the new file is not synchronized with the workspace and you should refresh the project in order to see it...
	 * @param f
	 * @param templateName
	 * @param params
	 * @throws IOException
	 */
	private static void createFileFromTemplate(File f, String templateName, Map<String, String> params) throws IOException{
		FileUtils.dump(new TemplateInputStream(params,
				resource("templates/" + templateName)), f);
	}

	
	/**
	 * create map from the given pairs
	 * @param params
	 * @return
	 */
	private static Map<String, String> buildParamMap(String... params) {
		Map<String, String> tkeys = new HashMap<String, String>();
		
		for (int i=0; i<params.length; i+=2){
			tkeys.put(params[i], params[i+1]);
		}
		return tkeys;
	}

	
	/**
	 * create new project on the file system
	 * 
	 * @param name
	 *            the new project name
	 * @return
	 */
	public static AgentZeroProject createNew(String name, IProgressMonitor mon) {
		try {
			// CREATE ECLIPSE PROJECT
			IWorkspaceRoot wroot = ResourcesPlugin.getWorkspace().getRoot();
			IProject proj = wroot.getProject(name);
			proj.create(mon);

			// OPEN THE PROJECT
			proj.open(mon);

			// DESCRIBE THE PROJECT AS ECLIPSE PROJECT + JAVA PROJECT + AGENT
			// ZERO PROJECT
			IProjectDescription description = proj.getDescription();
			description.setNatureIds(concat(description.getNatureIds(),
					AgentZeroProject.NATURE_ID, JavaCore.NATURE_ID));
			proj.setDescription(description, mon);

			// CREATE JAVA PROJECT
			IJavaProject jproj = JavaCore.create(proj);

			// CREATE AGENT ZERO PROJECT
			AgentZeroProject azproj = new AgentZeroProject(jproj);
			azproj.createDefaultProjectStructure(name);

			//CONFIGURE CLASS PATH			

			String jdpath = "";
			try {
				jdpath = "jar:" + SharedDataUnit.UNIT.getApiJavaDocZip().toURI().toURL().toExternalForm() + "!"
						+ SharedDataUnit.JAVADOC_LOCATION_IN_ARCHIVE;
			} catch (MalformedURLException e) {
				//should never happen...
				e.printStackTrace();
			}

			IClasspathAttribute[] azapiJavadoc = new IClasspathAttribute[] { JavaCore
					.newClasspathAttribute(
							IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME,
							jdpath) }
			;
			IClasspathEntry azapi = JavaCore.newLibraryEntry(new Path(
					SharedDataUnit.UNIT.getApiJar().getAbsolutePath()), null, null, null, azapiJavadoc,
					false);

			IClasspathEntry azcore = JavaCore.newLibraryEntry(new Path(SharedDataUnit.UNIT.getCoreJar().getAbsolutePath()), null, null);
			
			IClasspathEntry[] enteries = new IClasspathEntry[] {
					JavaRuntime.getDefaultJREContainerEntry(), //JAVA
					JavaCore.newSourceEntry(new Path("/" + name + "/src")), //SRC 
					azapi, azcore
			};

			jproj.setRawClasspath(enteries, mon);

			azproj.refreshInEclipse();
			return azproj;
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * refresh the project view in eclipse after modifying its content
	 */
	public void refreshInEclipse(){
		try {
			project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getProjectName() {
		return project.getProject().getName();
	}
	
	/**
	 * test a given project to see if it has the agent zero nature
	 * @param prj
	 * @return
	 */
	private static boolean isAgentZeroProject(IProject prj){
		try {
			return prj.hasNature(NATURE_ID);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
	}
	
	public static List<AgentZeroProject> allOpenedProjects(){
		IWorkspace w = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot wf = w.getRoot();
		IProject[] pjs = wf.getProjects();
		
		LinkedList<AgentZeroProject> ret = new LinkedList<AgentZeroProject>();
		for (IProject p : pjs){
			if (isAgentZeroProject(p) && p.isOpen()){
				ret.add(new AgentZeroProject(JavaCore.create(p)));
			}
		}
		
		return ret;
	}
	
	/**
	 * this method is very heavy and required new process execution each time it is called 
	 * so cache its result if you want to reuse it.
	 * @return
	 */
	public List<ScannedCodeUnit> findCodeUnits(){
		AntRunner runner = new AntRunner();
		runner.setBuildFileLocation(SharedDataUnit.UNIT.getCodeAnalyzerAntScript().getAbsolutePath());
		runner.addUserProperties(JavaUtils.assoc(new HashMap<String,String>(), "local.code", getRootDirectory().getAbsolutePath() + "/bin", "azlib", SharedDataUnit.AZ_WORKSPACE_PATH + "/lib"));
		runner.setArguments("-Dmessage=Building -verbose");
		try {
			runner.run(null);

		FileInputStream fis = new FileInputStream(new File(SharedDataUnit.UNIT.getCodeAnalyzerBaseFolder().getAbsolutePath() + "/out.txt"));
        ObjectInputStream ois = new ObjectInputStream(fis);
        List<ScannedCodeUnit> res = (List<ScannedCodeUnit>) ois.readObject();
        System.out.println("result of size: " + res.size());
        return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new LinkedList<ScannedCodeUnit>();
		}
		
	}	

}
