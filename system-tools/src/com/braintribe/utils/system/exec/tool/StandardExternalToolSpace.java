package com.braintribe.utils.system.exec.tool;

import java.nio.file.Path;

public class StandardExternalToolSpace implements ExternalToolSpace {

	private final String id;
	private final Path path;

	public StandardExternalToolSpace(String id, Path path) {
		this.id = id;
		this.path = path;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public Path path() {
		return path;
	}
}
