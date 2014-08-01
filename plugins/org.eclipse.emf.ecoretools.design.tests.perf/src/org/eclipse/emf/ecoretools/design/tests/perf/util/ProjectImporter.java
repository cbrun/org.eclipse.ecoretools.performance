/*******************************************************************************
 * Copyright (c)  2014 Obeo
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.ecoretools.design.tests.perf.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.ide.undo.DeleteResourcesOperation;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.ui.wizards.datatransfer.ZipFileStructureProvider;

public class ProjectImporter {
	

	protected static final IOverwriteQuery OVERWRITE_ALL_QUERY = new IOverwriteQuery() {
		public String queryOverwrite(String pathString) {
			return IOverwriteQuery.ALL;
		}
	};
	private boolean shouldDelete = true;;

	private Diagnostic deleteExistingProjects(IProgressMonitor monitor,
			List<ProjectDescriptor> projectDescriptors) {
		StringBuilder projectNames = new StringBuilder();
		List<IProject> projects = new ArrayList<IProject>();
		for (ProjectDescriptor projectDescriptor : projectDescriptors) {
			IProject project = projectDescriptor.getProject();
			if (project.exists()) {
				projectNames.append(", '").append(project.getName())
						.append("'");
				projects.add(project);
			}
		}

		if (!projects.isEmpty()) {
			projectNames.delete(0, ", ".length());

			if (shouldDelete) {
				DeleteResourcesOperation op = new DeleteResourcesOperation(
						projects.toArray(new IProject[projects.size()]),
						"deleteprojects", true);
				try {
					return BasicDiagnostic.toDiagnostic(op.execute(
							new SubProgressMonitor(monitor, 1), null));
				} catch (org.eclipse.core.commands.ExecutionException e) {
					return BasicDiagnostic.toDiagnostic(e);
				}
			} else {
				return Diagnostic.CANCEL_INSTANCE;
			}
		}
		return Diagnostic.OK_INSTANCE;
	}

	public void importInWorkspace(IProgressMonitor progressMonitor,
			List<ProjectDescriptor> projectDescriptors) throws Exception {
		deleteExistingProjects(progressMonitor, projectDescriptors);
		for (ProjectDescriptor projectDescriptor : projectDescriptors) {
			installProject(projectDescriptor, progressMonitor);
		}
		progressMonitor.done();
	}

	private void installProject(ProjectDescriptor projectDescriptor,
			ImportOperation importOperation, IProgressMonitor progressMonitor)
			throws Exception {
		createProject(projectDescriptor, new SubProgressMonitor(
				progressMonitor, 1));
		importOperation.run(new SubProgressMonitor(progressMonitor, 1));
	}

	private void createProject(ProjectDescriptor projectDescriptor,
			IProgressMonitor monitor) throws CoreException {

		IProject project = projectDescriptor.getProject();
		project.create(new SubProgressMonitor(monitor, 1));
		project.open(new SubProgressMonitor(monitor, 1));

	}

	private void installProject(ProjectDescriptor projectDescriptor,
			IProgressMonitor progressMonitor) throws Exception {
		URI contentURI = projectDescriptor.getContentURI();
		if (contentURI.hasTrailingPathSeparator()) {
			installProjectFromDirectory(projectDescriptor, progressMonitor);
		} else {
			installProjectFromFile(projectDescriptor, progressMonitor);
		}
	}

	private void installProjectFromDirectory(
			ProjectDescriptor projectDescriptor,
			IProgressMonitor progressMonitor) throws Exception {
		URI contentURI = projectDescriptor.getContentURI();
		if (contentURI.isPlatform()) {
			contentURI = CommonPlugin.asLocalURI(contentURI);
		}

		ImportOperation importOperation = null;
		String location = contentURI.toFileString();
		if (location != null) {
			File directory = new File(location);
			if (directory.isDirectory() && directory.canRead()) {
				List<File> filesToImport = new ArrayList<File>();
				filesToImport.addAll(Arrays.asList(directory.listFiles()));

				importOperation = new ImportOperation(
						projectDescriptor.getProject().getFullPath(),
						directory,
						org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider.INSTANCE,
						OVERWRITE_ALL_QUERY, filesToImport);
				importOperation.setCreateContainerStructure(false);
			}
		}

		if (importOperation != null) {
			installProject(projectDescriptor, importOperation, progressMonitor);
		} else {
			throw new Exception("The content directory '"
					+ contentURI.toString()
					+ "' does not exist or cannot be read");
		}
	}
	private void installProjectFromFile(ProjectDescriptor projectDescriptor,
			IProgressMonitor progressMonitor) throws Exception {
		URI contentURI = projectDescriptor.getContentURI();
		if (contentURI.isPlatform()) {
			contentURI = CommonPlugin.asLocalURI(contentURI);
		}

		ImportOperation importOperation = null;
		ZipFile zipFile = null;
		try {
			String location = contentURI.toFileString();
			if (location != null) {
				File file = new File(location);
				if (file.isFile() && file.canRead()) {
					zipFile = createZipFile(file);
					if (zipFile != null) {
						ZipFileStructureProvider structureProvider = new ZipFileStructureProvider(
								zipFile);
						importOperation = new ImportOperation(projectDescriptor
								.getProject().getFullPath(),
								structureProvider.getRoot(), structureProvider,
								OVERWRITE_ALL_QUERY);
					}
				}
			}

			if (importOperation != null) {
				installProject(projectDescriptor, importOperation,
						progressMonitor);
			} else {
				throw new Exception("The content file ''"
						+ contentURI.toString()
						+ "'' does not exist or is not supported.");
			}
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					// Ignore.
				}
			}
		}
	}

	private ZipFile createZipFile(File file) {
		try {
			return new ZipFile(file);
		} catch (ZipException e) {
			// Ignore
		} catch (IOException e) {
			// Ignore
		}
		return null;
	}
	

}
