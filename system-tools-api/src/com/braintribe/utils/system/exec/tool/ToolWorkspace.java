package com.braintribe.utils.system.exec.tool;

import java.nio.file.Path;

public interface ToolWorkspace {

	Path root();

	Path resolve(String relativePath);

	Path createDirectory(String relativePath);
}
