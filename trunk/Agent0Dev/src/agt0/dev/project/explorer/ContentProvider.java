package agt0.dev.project.explorer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import static agt0.dev.util.SWTUtils.*;

public class ContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//msgbox("input changed: oldInput=" + oldInput + ", newInput=" + newInput) ;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		//msgbox("blaaaa 2!");
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {
		//msgbox("blaaaa 4!");
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		//msgbox("blaaaa5!");
		return false;
	}

}
