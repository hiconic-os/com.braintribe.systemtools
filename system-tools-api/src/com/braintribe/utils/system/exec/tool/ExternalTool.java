package com.braintribe.utils.system.exec.tool;

public interface ExternalTool {

	String id();

	ToolAvailability availability();

	ToolDescriptor descriptor();

	ToolExecution openExecution();
}
