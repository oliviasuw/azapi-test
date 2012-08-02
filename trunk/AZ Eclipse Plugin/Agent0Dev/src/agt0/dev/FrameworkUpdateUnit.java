package agt0.dev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;

import agt0.dev.project.AgentZeroProject;
import agt0.dev.ui.update.AfterUpdateNotificationWindow;
import agt0.dev.ui.update.FirstUpdateWindow;
import agt0.dev.ui.update.UpdateAnywayWindow;
import agt0.dev.ui.update.UpdateAvailableWindow;
import agt0.dev.util.EclipseUtils;
import agt0.dev.util.FileUtils;

public enum FrameworkUpdateUnit {
	UNIT;

	private static final int CHUNK_SIZE = 1024 * 64;
	private static final String UPDATE_SITE = "http://azapi-test.googlecode.com/svn/trunk/bin/iteration";
	private static final String AZ_WORKSPACE = EclipseUtils
			.getWorkspaceDirectory() + "/.az";
	private static final long UPDATE_MONITOR_INTERVAL = 60 * 60 * 1000;

	private static final String VERSION_FILE_NAME = "version";
	private static final String UNKNOWN_VERSION = "unknown";
	private static final String UPDATE_FILE_NAME = "build.xml";
	private static final String CHANGE_LOG_FILE_NAME = "change-log";

	private String currentVersion = null;
	private String lastKnownVersion = null;

	public boolean isUpdateNeeded() {
		String curv = getCurrentVersion();
		String knownv = getLastKnownVersion();
		return !knownv.equals(UNKNOWN_VERSION)
				&& (curv.equals(UNKNOWN_VERSION) || !curv.equals(knownv));
	}

	public void startAutoUpdateMonitor() {
		new Job("Auto Update Monitor") {
			
			String lastCheckedVersion = UNKNOWN_VERSION;
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					if (SharedDataUnit.UNIT.isLibreryExists()) { //no update if no framework...
						clearVersionCache();
						System.out.println("autoupdate test known: " + getLastKnownVersion() + ", checked: " + lastCheckedVersion + ", current: " + getCurrentVersion());
						if (!getLastKnownVersion().equals(lastCheckedVersion) && isUpdateNeeded()){
							showUpdateAvailable();
							lastCheckedVersion = getLastKnownVersion(); // so no more questions
																// will be for
																// the user
																// until new
																// update or the
																// user press
																// update
						}
					}

					return Status.OK_STATUS;
				} finally {
					schedule(UPDATE_MONITOR_INTERVAL);
				}
			}
		}.schedule();
	}

	/**
	 * 
	 * will update the framework libs if needed see tutorial
	 * http://help.eclipse.
	 * org/galileo/index.jsp?topic=/org.eclipse.platform.doc.
	 * isv/guide/ant_running_buildfiles_programmatically.htm
	 * 
	 * @param monitor
	 * @return 1: success 0: no need -1: user cancel
	 */
	public int update(IProgressMonitor monitor, boolean force)
			throws UpdateException {
		try {
			monitor.beginTask("checking if update needed", 1);
			clearVersionCache();
			boolean need = isUpdateNeeded() || force;
			monitor.worked(1);

			if (monitor.isCanceled())
				return -1;

			if (need) {
				monitor.beginTask("downloading update instructions", 1);
				byte[] f = fetchUrl(UPDATE_SITE + "/" + UPDATE_FILE_NAME);
				if (f != null) {
					try {
						FileUtils.persist(new File(AZ_WORKSPACE),
								UPDATE_FILE_NAME, f);

						monitor.beginTask("running update script", 1);
						AntRunner runner = new AntRunner();
						runner.setBuildFileLocation(AZ_WORKSPACE + "/"
								+ UPDATE_FILE_NAME);
						runner.setArguments("-Dmessage=Building -verbose");
						runner.run(monitor);

						FileUtils.persist(new File(AZ_WORKSPACE),
								VERSION_FILE_NAME, getLastKnownVersion()
										.getBytes());
						clearVersionCache();
						refreshAllOpenedProjects();
					} catch (IOException e) {
						throw new UpdateException(
								"failed while retriving update script", e);
					} catch (CoreException e) {
						throw new UpdateException(
								"failed while executing update script", e);
					}
				}

			} else {
				return 0;
			}

			return 1;

		} finally {
			monitor.done();
		}

	}

	/**
	 * after updating this is use to refresh all the projects so that they will
	 * see the updates...
	 */
	private void refreshAllOpenedProjects() {
		for (AgentZeroProject p : AgentZeroProject.allOpenedProjects()) {
			p.refreshInEclipse();
		}
	}

	/**
	 * @return the current version of the libreries
	 */
	public String getCurrentVersion() {
		if (currentVersion == null) {
			try {
				currentVersion = FileUtils.unPersistText(new File(AZ_WORKSPACE
						+ "/" + VERSION_FILE_NAME));

				if (currentVersion == null)
					currentVersion = UNKNOWN_VERSION;
			} catch (Exception e) {
				currentVersion = UNKNOWN_VERSION;
				e.printStackTrace();
			}
		}

		return currentVersion;
	}

	/**
	 * @return the newest version of the libreries we ever retrived or if the
	 *         time is right check for version updates..
	 */
	public String getLastKnownVersion() {
		if (lastKnownVersion == null) {
			byte[] f = fetchUrl(UPDATE_SITE + "/" + VERSION_FILE_NAME);
			lastKnownVersion = (f == null ? UNKNOWN_VERSION : new String(f));
		}

		return lastKnownVersion;
	}

	/**
	 * if for some reason the version cache is messedup then call this function
	 * to clean it
	 */
	public void clearVersionCache() {
		lastKnownVersion = null;
		currentVersion = null;
	}

	public String fetchChangeLog() {
		byte[] f = fetchUrl(UPDATE_SITE + "/" + CHANGE_LOG_FILE_NAME);
		if (f == null)
			return "UNKNOWN DATA OR ERROR.";
		return new String(f);
	}

	/**
	 * fetch the returned data in memory from the given url
	 * 
	 * @param url
	 * @return
	 */
	private byte[] fetchUrl(String url) {
		try {
			LinkedList<byte[]> fetch = new LinkedList<byte[]>();
			URL u = new URL(url);
			InputStream in = u.openStream();
			byte[] chunk = new byte[CHUNK_SIZE];
			int read;
			int length = 0;

			while ((read = in.read(chunk)) >= 0) {
				if (read == chunk.length) {
					fetch.add(chunk);
					chunk = new byte[CHUNK_SIZE];
				} else {
					byte[] temp = new byte[read];
					System.arraycopy(chunk, 0, temp, 0, read);
					fetch.add(temp);
				}

				length += read;
			}

			byte[] ret = new byte[length];
			int fill = 0;
			while (!fetch.isEmpty()) {
				byte[] src = fetch.remove();
				System.arraycopy(src, 0, ret, fill, src.length);
				fill += src.length;
			}

			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * will notify the user if the first update is needed
	 */
	public void testForFirstUpdate() {
		if (!SharedDataUnit.UNIT.isLibreryExists()) {
			EclipseUtils.doInUiThread(new EclipseUtils.UIRunnable() {

				@Override
				public void run(IWorkbenchWindow window) {
					FirstUpdateWindow diag = new FirstUpdateWindow(window
							.getShell());
					diag.open();
				}
			}, true);
		}
	}

	/**
	 * will test if update is needed and notify the user
	 * 
	 * @param showAnyway
	 *            if true than the update anyway screen will be shown
	 */
	public void tryUpdate(final boolean showAnyway) {
		Job upjob = new Job("update agent zero") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				clearVersionCache();
				if (isUpdateNeeded()) {
					showUpdateAvailable();
				} else if (showAnyway) {
					showUpdateAnyway();
				}

				return Status.OK_STATUS;
			}
		};

		upjob.schedule();
	}

	private void showChangeLog() {
		// NOTIFY USER
		EclipseUtils.doInUiThread(new EclipseUtils.UIRunnable() {

			@Override
			public void run(IWorkbenchWindow window) {
				AfterUpdateNotificationWindow dialog = new AfterUpdateNotificationWindow(
						window.getShell(), FrameworkUpdateUnit.UNIT
								.getCurrentVersion(), FrameworkUpdateUnit.UNIT
								.fetchChangeLog());
				dialog.open();
			}
		}, false);
	}

	private void showUpdateAvailable() {
		// NOTIFY USER
		EclipseUtils.doInUiThread(new EclipseUtils.UIRunnable() {

			@Override
			public void run(IWorkbenchWindow window) {
				UpdateAvailableWindow dialog = new UpdateAvailableWindow(window
						.getShell());
				dialog.open();
			}
		}, false);
	}

	private void showUpdateAnyway() {
		// NOTIFY USER
		EclipseUtils.doInUiThread(new EclipseUtils.UIRunnable() {

			@Override
			public void run(IWorkbenchWindow window) {
				UpdateAnywayWindow dialog = new UpdateAnywayWindow(window
						.getShell());
				dialog.open();
			}
		}, false);
	}

	// private void showUpdateAvailable() {
	// //NOTIFY USER
	// EclipseUtils.doInUiThread(new EclipseUtils.UIRunnable() {
	//
	// @Override
	// public void run(IWorkbenchWindow window) {
	// UpdateQuestionWindow dialog = new UpdateQuestionWindow(
	// window.getShell());
	// dialog.open();
	// }
	// }, false);
	// }

	/**
	 * will start the update job - and notify user about status
	 */
	public void startUpdateJob(final boolean force) {
		Job upjob = new Job("update agent zero") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					FrameworkUpdateUnit.UNIT.update(monitor, force);

				} catch (UpdateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return Status.CANCEL_STATUS; // NEED TO NOTIFY USER
				}

				showChangeLog();

				return Status.OK_STATUS;
			}

		};

		upjob.setPriority(Job.LONG);
		upjob.schedule();

	}

	public static class UpdateException extends Exception {
		public UpdateException() {
			super();
		}

		public UpdateException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public UpdateException(String arg0) {
			super(arg0);
		}

		public UpdateException(Throwable arg0) {
			super(arg0);
		}
	}
}
