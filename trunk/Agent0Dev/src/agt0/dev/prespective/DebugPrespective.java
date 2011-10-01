package agt0.dev.prespective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;


public class DebugPrespective implements IPerspectiveFactory {

	/**
	 * Creates the initial layout for a page.
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		addFastViews(layout);
		addViewShortcuts(layout);
		addPerspectiveShortcuts(layout);
		layout.addView("org.eclipse.ui.navigator.ProjectExplorer", IPageLayout.LEFT, 0.2f, IPageLayout.ID_EDITOR_AREA);
		{
			IFolderLayout folderLayout = layout.createFolder("folder", IPageLayout.BOTTOM, 0.5f, IPageLayout.ID_EDITOR_AREA);
			folderLayout.addView("org.eclipse.ui.console.ConsoleView");
			//folderLayout.addView(AlgorithmExaminer.ID);
			folderLayout.addView("org.eclipse.debug.ui.ExpressionView");
			folderLayout.addView("org.eclipse.debug.ui.VariableView");
		}
		layout.addView("org.eclipse.debug.ui.DebugView", IPageLayout.TOP, 0.5f, "org.eclipse.ui.navigator.ProjectExplorer");
	}

	/**
	 * Add fast views to the perspective.
	 */
	private void addFastViews(IPageLayout layout) {
	}

	/**
	 * Add view shortcuts to the perspective.
	 */
	private void addViewShortcuts(IPageLayout layout) {
	}

	/**
	 * Add perspective shortcuts to the perspective.
	 */
	private void addPerspectiveShortcuts(IPageLayout layout) {
	}

}
