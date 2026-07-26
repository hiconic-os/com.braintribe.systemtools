package com.braintribe.utils.system.exec.tool;

public interface ToolExecution extends AutoCloseable {

	ToolWorkspace workspace();

	ToolExecutionResult execute(ToolExecutionRequest request) throws Exception;

	@Override
	void close();
}
