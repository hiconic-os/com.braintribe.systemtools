package com.braintribe.utils.system.exec.tool;

import java.util.List;

/**
 * Builds the effective command line for one invocation. Unlike a static command
 * prefix this may take the execution workspace into account, which is required
 * by container-backed tools.
 */
@FunctionalInterface
public interface ToolCommandLineBuilder {

	List<String> build(ToolWorkspace workspace, List<String> arguments);

}
