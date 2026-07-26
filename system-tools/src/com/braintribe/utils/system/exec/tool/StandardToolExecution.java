package com.braintribe.utils.system.exec.tool;

public class StandardToolExecution implements ToolExecution {

	private final ExternalTool tool;
	private final ToolExecutionScope scope;

	public StandardToolExecution(ExternalTool tool, ToolExecutionScope scope) {
		this.tool = tool;
		this.scope = scope;
	}

	@Override
	public ToolWorkspace workspace() {
		return scope.workspace();
	}

	@Override
	public ToolExecutionResult execute(ToolExecutionRequest request) throws Exception {
		return scope.execute(tool, request);
	}

	@Override
	public void close() {
		scope.close();
	}
}
