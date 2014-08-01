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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecoretools.design.tests.perf.TestProjects;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Iterators;

public class TableDescriptionTests {

	private static Session session;

	@BeforeClass
	public static void setUp() throws Exception {
		session = openModelingProject.apply(importInWorkspace
				.apply(TestProjects.reverseSirius10Times));
	}

	@Test
	public void createEditionTable() throws Exception {
		System.out.println("START !!!!");
		final EPackage smallEPackage = findSmallEPackage(session);
		assertNotNull(smallEPackage);
		String descriptionName = "Classes";

		final RepresentationDescription classes = findRepresentationDescription(
				smallEPackage, descriptionName);

		assertNotNull(classes);
		session.getTransactionalEditingDomain()
				.getCommandStack()
				.execute(
						new RecordingCommand(session
								.getTransactionalEditingDomain()) {

							@Override
							protected void doExecute() {
								DRepresentation table = DialectManager.INSTANCE
										.createRepresentation("small table",
												smallEPackage, classes,
												session,
												new NullProgressMonitor());
								assertNotNull(table);
								assertTrue(table
										.getOwnedRepresentationElements()
										.size() > 0);
							}
						});

	}

	protected RepresentationDescription findRepresentationDescription(
			final EPackage smallEPackage, String descriptionName) {
		RepresentationDescription found = null;
		Iterator<RepresentationDescription> it = DialectManager.INSTANCE
				.getAvailableRepresentationDescriptions(
						session.getSelectedViewpoints(true), smallEPackage)
				.iterator();
		while (found == null && it.hasNext()) {
			RepresentationDescription desc = it.next();
			if (descriptionName.equals(desc.getName())) {
				found = desc;
			}
		}
		return found;
	}

	private EPackage findSmallEPackage(Session s) {
		EPackage found = null;
		Iterator<Resource> itRes = s.getSemanticResources().iterator();
		while (found == null && itRes.hasNext()) {
			Iterator<EPackage> it = Iterators.filter(itRes.next()
					.getAllContents(), EPackage.class);
			while (it.hasNext() && found == null) {
				EPackage next = it.next();
				if (next.getEClassifiers().size() < 10
						&& next.getEClassifiers().size() > 0) {
					found = next;
				}
			}
		}
		return found;
	}

	@AfterClass
	public static void tearDown() {
		session.close(new NullProgressMonitor());
	}

}
