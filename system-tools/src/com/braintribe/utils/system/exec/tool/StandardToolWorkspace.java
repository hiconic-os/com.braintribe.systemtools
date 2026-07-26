package com.braintribe.utils.system.exec.tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StandardToolWorkspace implements ToolWorkspace {

	private final Path root;

	public StandardToolWorkspace(Path root) {
		this.root = root.toAbsolutePath().normalize();
	}

	@Override
	public Path root() {
		return root;
	}

	@Override
	public Path resolve(String relativePath) {
		if (relativePath == null)
			throw new IllegalArgumentException("Relative path must not be null.");

		Path relative = root.getFileSystem().getPath(relativePath);
		if (relative.isAbsolute())
			throw new IllegalArgumentException("Expected a relative workspace path, but got: " + relativePath);

		Path resolved = root.resolve(relative).normalize();
		if (!resolved.startsWith(root))
			throw new IllegalArgumentException("Workspace path escapes its root: " + relativePath);

		return resolved;
	}

	@Override
	public Path createDirectory(String relativePath) {
		Path directory = resolve(relativePath);
		try {
			return Files.createDirectories(directory);
		} catch (IOException e) {
			throw new IllegalStateException("Could not create tool workspace directory " + directory, e);
		}
	}
}
