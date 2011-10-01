package agt0.dev.util;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.Bundle;

import static agt0.dev.util.SWTUtils.*;
import agt0.dev.Activator;

public class PlatformUtils {
	private static Bundle BUNDLE = Platform.getBundle(Activator.PLUGIN_ID);

	public static InputStream resource(String path) {
		try {
			return FileLocator.openStream(
					Platform.getBundle(Activator.PLUGIN_ID), new Path(path),
					false);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> resourceFolderFileList(String path) {
		try {
			Enumeration<String> all = BUNDLE.getEntryPaths("*");
			LinkedList<String> ret = new LinkedList<String>();
			while (all.hasMoreElements()){
				ret.add(all.nextElement());
			}
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static ISelectionService selectionService(){
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
	}
	
	public static ISelection selection(){
		return selectionService().getSelection();	
	}
	
	public static IEditorPart activeEditor(){
		final IEditorPart[] box = new IEditorPart[1];
		inUIThread(new Runnable() {
			
			@Override
			public void run() {
				box[0] = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			}
		});
		
		return box[0];
	}
	
	public static CompilationUnitEditor activeCompilationUnitEditor(){
		return (CompilationUnitEditor) activeEditor();
	}
	
	public static IDocument innerDocument(IEditorPart editor){
		IDocumentProvider dp = ((ITextEditor) editor).getDocumentProvider();
		return dp.getDocument(editor.getEditorInput());
	}
	

	public static ICompilationUnit compilationUnit(IEditorPart editor){
		if (editor == null) return null;
		return (ICompilationUnit) JavaUI.getEditorInputJavaElement(editor.getEditorInput());
	}
	
	public static IWorkbenchPage activePage(){
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
	
	public static void openEditorFor(IFile file){
		IEditorDescriptor desc = PlatformUI.getWorkbench().
		        getEditorRegistry().getDefaultEditor(file.getName());
		try {
			activePage().openEditor(new FileEditorInput(file), desc.getId());
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
