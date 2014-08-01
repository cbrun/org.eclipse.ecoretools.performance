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

import java.util.Iterator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecoretools.design.tests.perf.TestProjects;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.DView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterators;

public class DDiagramTests {

	private DDiagram representation;
	private Session session;
	private int NB_ITERATIONS = 50;

	@Before
	public void setUp() throws Exception {
		session = openModelingProject.apply(importInWorkspace
				.apply(TestProjects.reverseSirius10Times));
		representation = (DDiagram) findRepresentationByName("104 EClasses class diagram");

	}

	protected DRepresentation findRepresentationByName(String diagramName) {
		DRepresentation found = null;
		Iterator<DView> itV = session.getSelectedViews().iterator();
		while (found == null && itV.hasNext()) {
			Iterator<DDiagram> it = Iterators.filter(itV.next().eAllContents(),
					DDiagram.class);
			while (it.hasNext() && found == null) {
				DDiagram nxt = it.next();
				if (diagramName.equals(nxt.getName())) {
					found = nxt;
				}
			}

		}
		return found;
	}

	@Test
	public void refresh() throws Exception {
		for (int i = 0; i < NB_ITERATIONS; i++) {
			DialectManager.INSTANCE.refresh(representation,
					new NullProgressMonitor());
		}
	}

	@Test
	public void delete() throws Exception {
		DialectManager.INSTANCE.deleteRepresentation(representation, session);
	}
	
	@After
	public void tearDown() {
		this.session.close(new NullProgressMonitor());
	}

	
}
