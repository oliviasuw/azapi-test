package bgu.dcr.az.cpu.server.utils;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class Ants {
	public static void build(String buildFilePath) throws BuildException {
		File buildFile = new File(buildFilePath);
		System.out.println("building " + buildFile.getAbsolutePath());
		Project p = new Project();
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget(p.getDefaultTarget());
	}
}
