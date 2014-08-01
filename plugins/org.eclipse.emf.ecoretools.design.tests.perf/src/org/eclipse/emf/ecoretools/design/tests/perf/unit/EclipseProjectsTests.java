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
package org.eclipse.emf.ecoretools.design.tests.perf.unit;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecoretools.design.tests.perf.TestProjects;
import org.eclipse.emf.ecoretools.design.tests.perf.util.ProjectDescriptor;
import org.eclipse.emf.ecoretools.design.tests.perf.util.ProjectImporter;
import org.eclipse.sirius.business.api.modelingproject.AbstractRepresentationsFileJob;
import org.eclipse.sirius.business.api.modelingproject.ModelingProject;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.ext.base.Option;
import org.eclipse.sirius.tests.support.api.SiriusDiagramTestCase;
import org.eclipse.sirius.ui.tools.api.project.ModelingProjectManager;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.google.common.collect.Lists;

public class EclipseProjectsTests {

	private ProjectDescriptor projectDescriptor;

	@Before
	public void setUp() throws Exception {
		projectDescriptor = TestProjects.reverseSiriusNonModeling;
		new ProjectImporter().importInWorkspace(new NullProgressMonitor(),
				Lists.newArrayList(projectDescriptor));
	}

	@Test
	public void testConvertToModelingProject() throws Exception {
		this.projectDescriptor.getProject().open(new NullProgressMonitor());
		ModelingProjectManager.INSTANCE.convertToModelingProject(
				this.projectDescriptor.getProject(), new NullProgressMonitor());
		Option<ModelingProject> optionalModelingProject = ModelingProject
				.asModelingProject(this.projectDescriptor.getProject());
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
				Job.getJobManager().join(AbstractRepresentationsFileJob.FAMILY,
						new NullProgressMonitor());
			}
		}
		assertTrue(SessionManager.INSTANCE.getSessions().size() > 0);
	}

	@Test
	public void testDeleteProject() throws Exception {
		this.projectDescriptor.getProject().close(new NullProgressMonitor());
	}

}
