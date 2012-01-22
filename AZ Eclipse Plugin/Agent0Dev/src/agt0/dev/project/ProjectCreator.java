package agt0.dev.project;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.internal.quickaccess.CamelUtil;

import agt0.dev.util.SourceUtils;
import agt0.dev.util.ds.TemplateInputStream;

import static agt0.dev.util.JavaUtils.*;
import static agt0.dev.util.PlatformUtils.*;

public enum ProjectCreator {
	INSTANCE;

	private static final String[] LIBS = new String[] { "AZAPI.jar",
			"AZCoreImpl.jar" };

	private static final String[] MISC = new String[] { "AbsoluteLayout.jar",
			"args4j-2.0.17.jar", "gson-1.7.1.jar", "AZDevImpl.jar",
			"BamCommon.jar", "BamUtils.jar", "DeepCopyUtil.jar", "gnujaxp.jar",
			"guava-10.0.1.jar", "h2-1.3.160.jar", "iText-2.1.5.jar",
			"javassist-3.8.0.GA.jar", "jcommon-1.0.16.jar",
			"jfreechart-1.0.13-experimental.jar", "jfreechart-1.0.13-swt.jar",
			"jfreechart-1.0.13.jar", "junit.jar", "reflections-0.9.5.jar",
			"servlet.jar", "slf4j-api-1.6.4.jar", "swingx-1.6.1.jar",
			"swtgraphics2d.jar", "xom-1.2.7.jar" };

	private static final String API_JAVADOC = "javadoc.zip";
	private static final String JAVADOC_LOCATION_IN_ARCHIVE = "/";

	public IJavaProject create1(String name, List<String> artifacts) {
		try {
			IProgressMonitor mon = null;
			IWorkspaceRoot wroot = ResourcesPlugin.getWorkspace().getRoot();
			IProject proj = wroot.getProject(name);
			proj.create(mon);
			proj.open(mon);
			IProjectDescription description = proj.getDescription();
			description.setNatureIds(concat(description.getNatureIds(),
					AgentZeroProject.NATURE_ID, JavaCore.NATURE_ID));
			proj.setDescription(description, mon);
			IJavaProject ret = JavaCore.create(proj);
			IPath srcPath = new Path("/" + name + "/src");

			buildFolderStracture(ret, name);
			IClasspathEntry[] enteries = new IClasspathEntry[] {
					JavaRuntime.getDefaultJREContainerEntry(),
					JavaCore.newSourceEntry(srcPath) };

			for (String s : LIBS)
				if (s.equals("AZAPI.jar")) {
					String jdpath = "";
					try {
						jdpath = ret.getProject()
								.findMember("/doc/" + API_JAVADOC)
								.getLocationURI().toURL().toExternalForm();
						jdpath = "jar:" + jdpath + "!"
								+ JAVADOC_LOCATION_IN_ARCHIVE;
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					IClasspathAttribute[] cpa = null;

					cpa = new IClasspathAttribute[] { JavaCore
							.newClasspathAttribute(
									IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME,
									jdpath) };
					IClasspathEntry entry = JavaCore.newLibraryEntry(new Path(
							"/" + name + "/lib/" + s), null, null, null, cpa,
							false);

					enteries = concat(enteries, entry);

				} else {

					enteries = concat(
							enteries,
							JavaCore.newLibraryEntry(new Path("/" + name
									+ "/lib/" + s), null, null));
				}

			ret.setRawClasspath(enteries, mon);

			return ret;
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}

	}

	private void buildFolderStracture(IJavaProject prj, String algoName)
			throws CoreException {
		IFile testx = prj.getProject().getFile("test.xml");
		if (testx.exists())
			testx.delete(true, null);

		algoName = SourceUtils.camelCase(algoName); // uc(algoName.charAt(0)) +
													// algoName.substring(1);
		Map<String, String> tkeys = assoc(new HashMap<String, String>(),
				"ALGORITHM_NAME", algoName);

		testx.create(new TemplateInputStream(tkeys,
				resource("templates/TESTING_CONFIGURATION_TEMPLATE")), true,
				null);

		IFolder srcf = prj.getProject().getFolder("src");
		srcf.create(true, true, null);

		IFolder packageRoot = srcf.getFolder("ext");
		packageRoot.create(true, true, null);
		packageRoot = packageRoot.getFolder("sim");
		packageRoot.create(true, true, null);

		IFolder agentsFolder = packageRoot.getFolder("agents");
		agentsFolder.create(true, true, null);
		packageRoot.getFolder("tools").create(true, true, null);
		packageRoot.getFolder("stracturedProblems").create(true, true, null);

		IFile firstFile = agentsFolder.getFile(algoName + "Agent.java");

		firstFile.create(new TemplateInputStream(tkeys,
				resource("templates/NEW_AGENT_TEMPLATE.java")), true, null);

		// LIB JARS
		IFolder lib = prj.getProject().getFolder("lib");
		lib.create(true, true, null);
		for (String cpath : LIBS) {
			lib.getFile(cpath).create(resource("cpath/" + cpath), true, null);
		}

		for (String cpath : MISC) {
			lib.getFile(cpath).create(resource("cpath/" + cpath), true, null);
		}

		// JAVADOCS
		IFolder doc = prj.getProject().getFolder("doc");
		doc.create(true, true, null);
		doc.getFile(API_JAVADOC).create(resource("cpath/" + API_JAVADOC), true,
				null);
	}
}
