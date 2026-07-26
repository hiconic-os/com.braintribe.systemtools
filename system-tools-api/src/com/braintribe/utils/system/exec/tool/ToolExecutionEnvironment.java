package com.braintribe.utils.system.exec.tool;

public interface ToolExecutionEnvironment {

	ExternalToolRegistry tools();

	ToolExecutionScope openScope();

	ExternalToolSpace externalSpace(String spaceId);
}
