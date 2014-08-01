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

import static org.eclipse.emf.ecoretools.design.tests.perf.util.Operations.importInWorkspace;
import static org.eclipse.emf.ecoretools.design.tests.perf.util.Operations.openModelingProject;
import static org.junit.Assert.*;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecoretools.design.editor.EcoreToolsViewpoints;
import org.eclipse.emf.ecoretools.design.tests.perf.TestProjects;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ui.business.api.viewpoint.ViewpointSelectionCallback;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SessionTests {

	private Session session;

	@Before
	public void setUp() throws Exception {
		session = openModelingProject.apply(importInWorkspace
				.apply(TestProjects.reverseSirius));
	}

//	@Test
//	public void testEnableDesignViewpoint() throws Exception {
//		enableViewpoint(EcoreToolsViewpoints.fromViewpointRegistry().design());
//		assertTrue(session.getSelectedViewpoints(false).size() == 1);
//	}
//
	@Test
	public void testEnableDesignThenArchetypeViewpoint() throws Exception {
		enableViewpoint(EcoreToolsViewpoints.fromViewpointRegistry().design());
		enableViewpoint(EcoreToolsViewpoints.fromViewpointRegistry()
				.archetype());
		assertEquals(2,session.getSelectedViewpoints(false).size());
	}
//
//	@Test
//	public void testEnableReviewViewpoint() throws Exception {
//		enableViewpoint(EcoreToolsViewpoints.fromViewpointRegistry().review());
//		assertTrue(session.getSelectedViewpoints(false).size() == 1);
//	}
//
//	@Test
//	public void testEnableArchetypeViewpoint() throws Exception {
//		enableViewpoint(EcoreToolsViewpoints.fromViewpointRegistry()
//				.archetype());
//		assertTrue(session.getSelectedViewpoints(false).size() == 1);
//	}
//
//	@Test
//	public void testEnableGenerationViewpoint() throws Exception {
//		enableViewpoint(EcoreToolsViewpoints.fromViewpointRegistry()
//				.generation());
//		assertTrue(session.getSelectedViewpoints(false).size() == 1);
//	}

	protected void enableViewpoint(final Viewpoint viewpoint) {
		final ViewpointSelectionCallback cback = new ViewpointSelectionCallback();
		session.getTransactionalEditingDomain()
				.getCommandStack()
				.execute(
						new RecordingCommand(session
								.getTransactionalEditingDomain()) {

							@Override
							protected void doExecute() {
								cback.selectViewpoint(viewpoint, session,
										new NullProgressMonitor());

							}
						});
	}

	protected void disableViewpoint(final Viewpoint viewpoint) {
		final ViewpointSelectionCallback cback = new ViewpointSelectionCallback();
		session.getTransactionalEditingDomain()
				.getCommandStack()
				.execute(
						new RecordingCommand(session
								.getTransactionalEditingDomain()) {

							@Override
							protected void doExecute() {
								cback.deselectViewpoint(viewpoint, session,
										new NullProgressMonitor());

							}
						});
	}

	@After
	public void tearDown() {
		this.session.close(new NullProgressMonitor());
	}

}
