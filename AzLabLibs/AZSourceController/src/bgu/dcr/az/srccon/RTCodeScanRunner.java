package bgu.dcr.az.srccon;

import bgu.dcr.az.db.ent.Code;
import java.io.File;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class RTCodeScanRunner {

    private static void updateCodeTypes(File buildFile, File azLib, List<String> classesToUpdate, File tempFolder) throws BuildException {
        Project p = new Project();
        p.setUserProperty("ant.file", buildFile.getAbsolutePath());
        p.setUserProperty("azlib", azLib.getAbsolutePath());
        p.setUserProperty("output.file", tempFolder.getAbsolutePath() + "/output.file");
        StringBuilder sb = new StringBuilder();
        for (String code : classesToUpdate){
            sb.append(code).append(" ");
        }
        sb.deleteCharAt(sb.length()-1);
        p.setUserProperty("classes.to.scan", sb.toString());
        p.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        p.addReference("ant.projectHelper", helper);
        helper.parse(p, buildFile);
        p.executeTarget(p.getDefaultTarget());
    }
    
    public static void main(String[] args){
        scan(new File("package2.xml"), new File("blablabla"), );
    }
}
