package bgu.dcr.az.lab.servlet;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import bgu.dcr.az.db.ent.User;
import bgu.dcr.az.lab.DB;
import bgu.dcr.az.lab.FileSystem;
import bgu.dcr.az.lab.beans.CodeUploader;
import bgu.dcr.az.lab.codes.PackageReadFailedException;
import bgu.dcr.az.lab.codes.SourcePackage;
import bgu.dcr.az.lab.exp.UnRecognizedUserException;
import bgu.dcr.az.lab.util.FacesUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author miclando
 */
public class FileUploadServlet extends HttpServlet {

    private static final long serialVersionUID = -3208409086358916855L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        DB db = (DB) getServletContext().getAttribute("dbManager");
        FileSystem fs = (FileSystem) getServletContext().getAttribute("fileSystem");

        if (db == null || fs == null) {
            sendError("db or fs were null", response);
            return;
        }
        if (isMultipart) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                System.out.println("userName is: " + request.getHeader("user"));
                System.out.println("userName is: " + request.getHeader("password"));
                String userName = request.getHeader("user");
                String userPass = request.getHeader("password");
                if (userName == null || userName.isEmpty()) {
                    sendError("wrong user", response);
                    return;
                }

                User user = User.getByEmail(userName, db);
                if (user == null) {
                    sendError("no such user", response);
                    return;
                }
                List items = upload.parseRequest(request);
                Iterator iterator = items.iterator();
                while (iterator.hasNext()) {
                    FileItem item = (FileItem) iterator.next();

                    if (!item.isFormField()) {

                        String tempFolder = fs.getTemporaryFolder().getAbsolutePath();
                        String fileName = item.getName();
                        File uploadedFile = new File(tempFolder + "/" + fileName);
                        item.write(uploadedFile);
                        SourcePackage sp = new SourcePackage(uploadedFile, fs, user);
                        sp.saveToDB(db);


                    }
                }

            } catch (UnRecognizedUserException ex) {
                ex.printStackTrace();
                sendError("failed parsing the file, wrong user", response);
                return;
            } catch (PackageReadFailedException ex) {
                ex.printStackTrace();
                sendError("failed parsing the file", response);
                return;
            } catch (FileUploadException ex) {
                ex.printStackTrace();
                sendError("failed uploading the file", response);
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
                sendError("some thing wrong heppend: " + ex.getMessage(), response);
                return;
            }
        }


    }

    private void sendError(String errContent, HttpServletResponse response) throws IOException {
        response.setStatus(500);
        response.getOutputStream().write("something went really wrong".getBytes());
        response.getOutputStream().flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.getWriter().write("hello world");
    }
}
