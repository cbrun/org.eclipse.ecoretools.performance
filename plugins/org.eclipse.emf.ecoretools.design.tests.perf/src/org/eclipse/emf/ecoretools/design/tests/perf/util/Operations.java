package org.eclipse.emf.ecoretools.design.tests.perf.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.sirius.business.api.modelingproject.AbstractRepresentationsFileJob;
import org.eclipse.sirius.business.api.modelingproject.ModelingProject;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.internal.session.danalysis.SaveSessionJob;
import org.eclipse.sirius.common.tools.api.resource.ResourceSetSync;
import org.eclipse.sirius.ext.base.Option;
import org.eclipse.sirius.ui.tools.api.project.ModelingProjectManager;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class Operations {

	public static Function<ProjectDescriptor, IProject> importInWorkspace = new Function<ProjectDescriptor, IProject>() {

		@Override
		public IProject apply(ProjectDescriptor input) {
			try {
				new ProjectImporter().importInWorkspace(
						new NullProgressMonitor(), Lists.newArrayList(input));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return input.getProject();
		}
	};

	public static Function<IProject, Session> openModelingProject = new Function<IProject, Session>() {

		@Override
		public Session apply(IProject input) {
			try {
				ModelingProjectManager.INSTANCE.convertToModelingProject(input,
						new NullProgressMonitor());
				Option<ModelingProject> optionalModelingProject = ModelingProject
						.asModelingProject(input);
				if (optionalModelingProject.some()) {
					Option<URI> optionalMainSessionFileURI = optionalModelingProject
							.get().getMainRepresentationsFileURI(
									new NullProgressMonitor(), false, false);
					if (optionalMainSessionFileURI.some()) {
						// Load the main representations file of this modeling
						// project if it's not already loaded or during loading.
						ModelingProjectManager.INSTANCE
								.loadAndOpenRepresentationsFile(optionalMainSessionFileURI
										.get());
						Job.getJobManager().join(
								AbstractRepresentationsFileJob.FAMILY,
								new NullProgressMonitor());
						Job.getJobManager().join(
								SaveSessionJob.FAMILY,
								new NullProgressMonitor());
						Job.getJobManager().join(
								org.eclipse.sirius.common.tools.internal.resource.ResourceSyncClientNotifier.FAMILY,
								new NullProgressMonitor());
						return optionalModelingProject.get().getSession();
					}
				}
			} catch (CoreException e) {
				throw new RuntimeException(e);
			} catch (OperationCanceledException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return null;
		}
	};

}
