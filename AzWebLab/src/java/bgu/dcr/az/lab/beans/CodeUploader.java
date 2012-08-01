/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.beans;

import bgu.dcr.az.db.ent.Code;
import bgu.dcr.az.db.ent.CodeType;
import bgu.dcr.az.db.ent.User;
import bgu.dcr.az.lab.Laboratory;
import bgu.dcr.az.lab.codep.Scanner;
import bgu.dcr.az.lab.util.Files;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Inka
 */
@ManagedBean(name="codeUploader")
@ViewScoped
public class CodeUploader {

    public static final String AGENTS_PATH = "\\src\\ext\\sim\\agents";
    public static final String MODULES_PATH = "\\src\\ext\\sim\\modules";
    public static final String TOOLS_PATH = "\\src\\ext\\sim\\tools";
    @ManagedProperty("#{login}")
    private Login login;
    private UploadedFile file;
    private List<RowCode> classes;
    private RowCode[] selectedClasses;
    private HashMap<RowCode, Object> rowCodeToObject;

    public UploadedFile getFile() {
        return file;
    }

    public List<RowCode> getClasses() {
        return classes;
    }

    public RowCode[] getSelectedClasses() {
        return selectedClasses;
    }

    public HashMap<RowCode, Object> getRowCodeToCode() {
        return rowCodeToObject;
    }

    public void setClasses(List<RowCode> classes) {
        this.classes = classes;
    }

    public void setSelectedClasses(RowCode[] selectedClasses) {
        this.selectedClasses = selectedClasses;
    }

    public void setRowCodeToCode(HashMap<RowCode, Object> rowCodeToCode) {
        this.rowCodeToObject = rowCodeToCode;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Login getLogin() {
        return login;
    }

    public void handleFileUpload(FileUploadEvent event) {
        System.out.println("event is " + event.toString());
        if (event.getFile() != null) {
            saveFile(event.getFile());
        }
    }

    private void handleOpenFiles(File where) throws IOException {
        Map<String, List<File>> toolsLocation = scanToolsForDependencies(where);
        LinkedList<Code> agentsList = new LinkedList<Code>();
        LinkedList<Code> modulesList = new LinkedList<Code>();
        LinkedList<Code> toolsList = new LinkedList<Code>();

        Map<File, Code> toolsToCodeMap = createCodes(where, agentsList, modulesList, toolsList);
        updateDependencies(toolsList, toolsToCodeMap, toolsLocation);
        updateDependencies(agentsList, toolsToCodeMap, toolsLocation);
        updateDependencies(modulesList, toolsToCodeMap, toolsLocation);

        fillClassesAndSelectedLists(agentsList, toolsList, modulesList);
    }

    private void createCode(File f, Map<File, Code> toolsToCodeMap, List<Code> agents, List<Code> modules, List<Code> tools) throws IOException {
        Code code = new Code();
        code.setAuthor(login.getUser());
        code.setLocationOnDisk(f);
        code.setName(f.getName().substring(0, f.getName().length() - 5));
        code.setSafe(login.getUser().getRole().getSafety());
        code.setDescription(Scanner.extractClassDescription(f));

        if (f.getAbsolutePath().contains(AGENTS_PATH)) {
            code.setType(CodeType.AGENT);
            agents.add(code);
        } else if (f.getAbsolutePath().contains(TOOLS_PATH)) {
            code.setType(CodeType.TOOL);
            tools.add(code);
            toolsToCodeMap.put(f, code);
        } else if (f.getAbsolutePath().contains(MODULES_PATH)) {
            code.setType(findModuleType(f));
            modules.add(code);
        }
    }

    private Map<String, List<File>> scanToolsForDependencies(File where) throws IOException {
        Map<String, List<File>> toolsLocation = new HashMap<String, List<File>>();
        LinkedList<File> dirsToRead = new LinkedList<File>();
        dirsToRead.add(new File(where.getAbsolutePath() + "/src/ext/sim/tools"));
        while (!dirsToRead.isEmpty()) {
            for (File f : dirsToRead.remove().listFiles()) {
                if (f.isDirectory()) {
                    dirsToRead.add(f);
                } else {
                    if (f.getName().endsWith(".java")) {
                        List<String> list = Scanner.extractClasses(f);
                        for (String cls : list) {
                            List<File> l = toolsLocation.get(cls);
                            if (l == null) {
                                l = new LinkedList<File>();
                                toolsLocation.put(cls, l);
                            }

                            l.add(f);
                        }
                    }
                }
            }
        }
        return toolsLocation;
    }

    private void updateDependencies(List<Code> codes, Map<File, Code> toolsToCodeMap, Map<String, List<File>> toolsLocation) throws IOException {
        for (Code code : codes) {
            Set<File> list = Scanner.extractDependencies(code.getLocationOnDisk(), toolsLocation);
            for (File tool : list) {
//                code.getDependencies().add(toolsToCodeMap.get(tool));
            }
        }

    }

    private Map<File, Code> createCodes(File where, List<Code> agents, List<Code> modules, List<Code> tools) throws IOException {
        Map<File, Code> toolsToCodeMap = new HashMap<File, Code>();
        LinkedList<File> dirsToRead = new LinkedList<File>();
        dirsToRead.add(new File(where.getAbsolutePath() + AGENTS_PATH));
        dirsToRead.add(new File(where.getAbsolutePath() + MODULES_PATH));
        dirsToRead.add(new File(where.getAbsolutePath() + TOOLS_PATH));
        while (!dirsToRead.isEmpty()) {
            for (File f : dirsToRead.remove().listFiles()) {
                if (f.isDirectory()) {
                    dirsToRead.add(f);
                } else {
                    if (f.getName().endsWith(".java")) {
                        createCode(f, toolsToCodeMap, agents, modules, tools);
                    }
                }
            }
        }
        return toolsToCodeMap;
    }

    private void fillClassesAndSelectedLists(LinkedList<Code> agentsList, LinkedList<Code> toolsList, LinkedList<Code> modulesList) {
        classes = new LinkedList<RowCode>();
        selectedClasses = new RowCode[agentsList.size() + modulesList.size() + toolsList.size()];
        rowCodeToObject = new HashMap<RowCode, Object>();
        int i = 0;
        RowCode rowCode;
        for (Code c : agentsList) {
            rowCode = new RowCode(i, c.getName(), "Agent");
            classes.add(rowCode);
            selectedClasses[i++] = rowCode;
            rowCodeToObject.put(rowCode, c);
        }
        for (Code c : toolsList) {
            rowCode = new RowCode(i, c.getName(), "Tool");
            classes.add(rowCode);
            selectedClasses[i++] = rowCode;
            rowCodeToObject.put(rowCode, c);
        }
        for (Code c : modulesList) {
            rowCode = new RowCode(i, c.getName(), "Module");
            classes.add(rowCode);
            selectedClasses[i++] = rowCode;
            rowCodeToObject.put(rowCode, c);
        }
    }

    public String saveCode() {
        System.out.println("Selected are: ");
        for (int i = 0; i < selectedClasses.length; i++) {
            System.out.println(selectedClasses[i].getCodeName());
        }
        return "login";
    }

    private CodeType findModuleType(File f) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void saveFile(UploadedFile file) {
        this.file = file;
        FacesMessage msg = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        System.out.println("File is " + file.getFileName());
        try {
            User u = login.getUser();
            File tempFolder = new File(Laboratory.BASE_PATH + "/" + u.getId() + "/temp");
            tempFolder.mkdirs();
            Files.persist(tempFolder, "zip.zip", file.getContents());
            File zip = new File(tempFolder.getAbsolutePath() + "/zip.zip");
            final File openFiles = new File(tempFolder.getAbsolutePath() + "/open");
            Files.unzip(zip, openFiles);
            handleOpenFiles(openFiles);
        } catch (IOException ex) {
            //tell the client something...
            Logger.getLogger(CodeUploader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
