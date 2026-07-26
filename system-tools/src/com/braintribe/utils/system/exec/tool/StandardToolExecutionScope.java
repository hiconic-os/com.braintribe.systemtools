package com.braintribe.utils.system.exec.tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import com.braintribe.logging.Logger;

public class StandardToolExecutionScope implements ToolExecutionScope {

	private static final Logger logger = Logger.getLogger(StandardToolExecutionScope.class);

	private final StandardToolExecutionEnvironment environment;
	private final ToolWorkspace workspace;
	private boolean closed;

	public StandardToolExecutionScope(StandardToolExecutionEnvironment environment, Path root) {
		this.environment = environment;
		this.workspace = new StandardToolWorkspace(root);
	}

	@Override
	public ToolWorkspace workspace() {
		ensureOpen();
		return workspace;
	}

	@Override
	public ToolExecutionResult execute(ExternalTool tool, ToolExecutionRequest request) throws Exception {
		ensureOpen();
		if (!(tool instanceof StandardExternalTool))
			throw new IllegalArgumentException("Tool was not created by a compatible tool execution environment: " + tool.id());

		StandardExternalTool standardTool = (StandardExternalTool) tool;
		if (standardTool.environment() != environment)
			throw new IllegalArgumentException("Tool and execution scope belong to different environments: " + tool.id());

		return standardTool.execute(this, request);
	}

	@Override
	public void close() {
		if (closed)
			return;
		closed = true;

		Path root = workspace.root();
		if (!Files.exists(root))
			return;

		try (Stream<Path> paths = Files.walk(root)) {
			paths.sorted(Comparator.reverseOrder()).forEach(this::deleteSilently);
		} catch (Exception e) {
			logger.warn("Could not completely clean tool execution workspace " + root, e);
		}
	}

	private void deleteSilently(Path path) {
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			logger.warn("Could not delete tool execution workspace path " + path, e);
		}
	}

	private void ensureOpen() {
		if (closed)
			throw new IllegalStateException("Tool execution scope is already closed.");
	}
}
