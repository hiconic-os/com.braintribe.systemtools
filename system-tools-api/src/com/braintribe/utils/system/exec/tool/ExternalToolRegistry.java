package com.braintribe.utils.system.exec.tool;

import java.util.Collection;

public interface ExternalToolRegistry {

	ExternalTool required(String toolId);

	ExternalTool optional(String toolId);

	Collection<ToolDescriptor> descriptors();
}
