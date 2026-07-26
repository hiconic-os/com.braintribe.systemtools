package com.braintribe.utils.system.exec.tool;

import java.util.Collections;

public class UnavailableExternalTool implements ExternalTool {

	private final String id;
	private final String reason;

	public UnavailableExternalTool(String id, String reason) {
		this.id = id;
		this.reason = reason;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public ToolAvailability availability() {
		return ToolAvailability.UNAVAILABLE;
	}

	@Override
	public ToolDescriptor descriptor() {
		return new ToolDescriptor(id, "unconfigured", Collections.<String>emptyList(), ToolAvailability.UNAVAILABLE, reason);
	}

	@Override
	public ToolExecution openExecution() {
		throw new IllegalStateException("External tool '" + id + "' is unavailable: " + reason);
	}
}
