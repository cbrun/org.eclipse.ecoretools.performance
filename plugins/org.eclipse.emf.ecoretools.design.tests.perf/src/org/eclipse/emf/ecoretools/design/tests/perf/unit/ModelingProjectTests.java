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

import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecoretools.design.tests.perf.TestProjects;
import org.eclipse.emf.ecoretools.design.tests.perf.util.Operations;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ui.tools.api.project.ModelingProjectManager;
import org.junit.Before;
import org.junit.Test;

public class ModelingProjectTests {

	private IProject project;

	@Before
	public void setUp() throws Exception {
		project = Operations.importInWorkspace
				.apply(TestProjects.reverseSirius10Times);
	}

	@Test
	public void testOpenModelingProject() throws Exception {
		this.project.getProject().open(new NullProgressMonitor());
		ModelingProjectManager.INSTANCE.convertToModelingProject(
				this.project.getProject(), new NullProgressMonitor());
		Session session = Operations.openModelingProject.apply(this.project);
		assertTrue(session.isOpen());
		session.close(new NullProgressMonitor());
	}

	@Test
	public void testCloseProject() throws Exception {
		this.project.getProject().close(new NullProgressMonitor());
	}

}
