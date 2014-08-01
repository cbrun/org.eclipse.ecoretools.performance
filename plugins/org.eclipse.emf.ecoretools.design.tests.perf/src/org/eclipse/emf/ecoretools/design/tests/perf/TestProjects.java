package org.eclipse.emf.ecoretools.design.tests.perf;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecoretools.design.tests.perf.util.ProjectDescriptor;

public class TestProjects {

	public static ProjectDescriptor reverseSiriusNonModeling = new ProjectDescriptor(
			"reverse-sirius",
			URI.createURI("projects/reverse-sirius/"),
			"An Ecore model reverse engineering the Sirius code. This is not a modeling project.");
	
	public static ProjectDescriptor reverseSirius = new ProjectDescriptor(
			"reverse-sirius-modeling",
			URI.createURI("projects/reverse-sirius-modeling/"),
			"An Ecore model reverse engineering the Sirius code. ");

	public static ProjectDescriptor reverseSirius10Times = new ProjectDescriptor(
			"reverse-sirius-10times",
			URI.createURI("projects/reverse-sirius-10times/"),
			"An Ecore model reverse engineering the Sirius code copy/pasted 10 times.");

}
