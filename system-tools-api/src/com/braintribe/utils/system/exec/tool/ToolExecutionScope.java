package com.braintribe.utils.system.exec.tool;

public interface ToolExecutionScope extends AutoCloseable {

	ToolWorkspace workspace();

	ToolExecutionResult execute(ExternalTool tool, ToolExecutionRequest request) throws Exception;

	@Override
	void close();
}
