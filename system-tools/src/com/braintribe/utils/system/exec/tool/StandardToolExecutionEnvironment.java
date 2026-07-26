package com.braintribe.utils.system.exec.tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import com.braintribe.utils.system.exec.CommandExecution;

public class StandardToolExecutionEnvironment implements ToolExecutionEnvironment, ExternalToolRegistry {

	private final CommandExecution commandExecution;
	private final Path root;
	private final Path workspacesRoot;
	private final Path externalRoot;
	private final String backend;
	private final Map<String, ExternalTool> tools = new LinkedHashMap<String, ExternalTool>();

	public StandardToolExecutionEnvironment(CommandExecution commandExecution, Path root, String backend) {
		if (commandExecution == null)
			throw new IllegalArgumentException("Command execution must not be null.");
		if (root == null)
			throw new IllegalArgumentException("Tool file system root must not be null.");

		this.commandExecution = commandExecution;
		this.root = root.toAbsolutePath().normalize();
		this.workspacesRoot = this.root.resolve("workspaces");
		this.externalRoot = this.root.resolve("external");
		this.backend = backend == null ? "local" : backend;
		createDirectories(workspacesRoot);
		createDirectories(externalRoot);
	}

	public ExternalTool register(String toolId, String... command) {
		final List<String> parts = new ArrayList<String>();
		if (command != null)
			Collections.addAll(parts, command);
		return register(toolId, new Supplier<List<String>>() {
			@Override
			public List<String> get() {
				return parts;
			}
		});
	}

	public ExternalTool register(String toolId, Supplier<List<String>> commandResolver) {
		return register(toolId, commandResolver, null);
	}

	public ExternalTool register(String toolId, Supplier<List<String>> commandResolver, ToolCommandLineBuilder commandLineBuilder) {
		validateId(toolId);
		if (tools.containsKey(toolId))
			throw new IllegalStateException("External tool '" + toolId + "' is already registered.");
		StandardExternalTool tool = new StandardExternalTool(toolId, backend, this, commandResolver, commandLineBuilder);
		tools.put(toolId, tool);
		return tool;
	}

	@Override
	public ExternalToolRegistry tools() {
		return this;
	}

	@Override
	public ToolExecutionScope openScope() {
		Path workspace = workspacesRoot.resolve(UUID.randomUUID().toString());
		createDirectories(workspace);
		return new StandardToolExecutionScope(this, workspace);
	}

	@Override
	public ExternalToolSpace externalSpace(String spaceId) {
		validateId(spaceId);
		Path path = externalRoot.resolve(spaceId).normalize();
		if (!path.startsWith(externalRoot))
			throw new IllegalArgumentException("External tool space escapes its root: " + spaceId);
		createDirectories(path);
		return new StandardExternalToolSpace(spaceId, path);
	}

	@Override
	public ExternalTool required(String toolId) {
		ExternalTool tool = optional(toolId);
		if (tool.availability() != ToolAvailability.AVAILABLE)
			throw new IllegalStateException("Required external tool '" + toolId + "' is unavailable: " + tool.descriptor().getAvailabilityMessage());
		return tool;
	}

	@Override
	public ExternalTool optional(String toolId) {
		validateId(toolId);
		ExternalTool tool = tools.get(toolId);
		if (tool == null)
			return new UnavailableExternalTool(toolId, "No tool mapping is configured.");
		return tool;
	}

	@Override
	public Collection<ToolDescriptor> descriptors() {
		List<ToolDescriptor> result = new ArrayList<ToolDescriptor>(tools.size());
		for (ExternalTool tool : tools.values())
			result.add(tool.descriptor());
		return Collections.unmodifiableList(result);
	}

	CommandExecution commandExecution() {
		return commandExecution;
	}

	public Path root() {
		return root;
	}

	private void createDirectories(Path path) {
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			throw new IllegalStateException("Could not create tool file system directory " + path, e);
		}
	}

	private void validateId(String id) {
		if (id == null || id.trim().isEmpty())
			throw new IllegalArgumentException("Tool or space id must not be blank.");
		if (!id.matches("[A-Za-z0-9][A-Za-z0-9._-]*"))
			throw new IllegalArgumentException("Invalid tool or space id: " + id);
	}
}
