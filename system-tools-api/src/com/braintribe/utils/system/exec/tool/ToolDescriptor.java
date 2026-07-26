package com.braintribe.utils.system.exec.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToolDescriptor {

	private final String id;
	private final String backend;
	private final List<String> command;
	private final ToolAvailability availability;
	private final String availabilityMessage;

	public ToolDescriptor(String id, String backend, List<String> command, ToolAvailability availability, String availabilityMessage) {
		this.id = id;
		this.backend = backend;
		this.command = command == null ? Collections.<String>emptyList() : Collections.unmodifiableList(new ArrayList<String>(command));
		this.availability = availability;
		this.availabilityMessage = availabilityMessage;
	}

	public String getId() {
		return id;
	}

	public String getBackend() {
		return backend;
	}

	public List<String> getCommand() {
		return command;
	}

	public ToolAvailability getAvailability() {
		return availability;
	}

	public String getAvailabilityMessage() {
		return availabilityMessage;
	}
}
