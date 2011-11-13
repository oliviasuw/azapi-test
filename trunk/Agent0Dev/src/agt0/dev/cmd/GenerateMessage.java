package agt0.dev.cmd;

import static agt0.dev.util.PlatformUtils.activeEditor;
import static agt0.dev.util.PlatformUtils.compilationUnit;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import agt0.dev.Constants;

import static agt0.dev.util.JavaUtils.*;
import static agt0.dev.util.PlatformUtils.*;
import static agt0.dev.util.SWTUtils.*;
import static agt0.dev.util.SourceUtils.*;

public class GenerateMessage implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("restriction")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ICompilationUnit unit = compilationUnit(activeEditor());

		final String messageName = inputBox("Create New Message",
				"Message Name");

		if (messageName == null)
			return null;
		String camel = camelCase(messageName);
		final String methodName = "handle" + camel;

		IType type = null;
		try {
			unit.createImport(Constants.WHEN_RECEIVED_CLASS_NAME.stringData(), null, null);
			type = unit.getType(drop(unit.getElementName(), 4));
			
			if (!type.getSuperclassName().equals("SimpleAgent")) {
				msgbox("cannot create message - unrecognized agent type");
				return null;
			}
			
			IMethod mtd = method(type, methodName);

			if (mtd == null) {
				mtd = type
						.createMethod(
								"@WhenReceived(\""
										+ messageName
										+ "\")\npublic void "
										+ methodName
										+ "(){\n\t//TODO: Add Message Handling Code Here.\n\t"
										+ "//you can add any parameters to the method in order to receive them within the message.\n}\n",
								null, false, null);
			}

			CompilationUnitEditor editor = activeCompilationUnitEditor();
			editor.setSelection(mtd);

		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean isEnabled() {
		return activeEditor() != null;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
