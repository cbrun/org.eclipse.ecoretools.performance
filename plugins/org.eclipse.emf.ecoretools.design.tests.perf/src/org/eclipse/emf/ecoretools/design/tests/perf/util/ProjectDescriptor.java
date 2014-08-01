package org.eclipse.emf.ecoretools.design.tests.perf.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;

public class ProjectDescriptor {
	protected String name;
	protected URI contentURI;
	protected String description;

	public ProjectDescriptor(String name, URI contentURI, String description) {
		super();
		this.name = name;
		this.contentURI = contentURI;
		this.description = description;
	}

	protected IProject project;

	public URI getContentURI() {
		return contentURI;
	}

	public void setContentURI(URI contentURI) {
		this.contentURI = contentURI;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public IProject getProject() {
		if (project == null) {
			project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(getName());
		}
		return project;
	}
}