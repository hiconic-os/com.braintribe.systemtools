package com.braintribe.utils.system.exec.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.braintribe.utils.system.exec.RunCommandContext;
import com.braintribe.utils.system.exec.RunCommandRequest;

public class StandardExternalTool implements ExternalTool {

	private final String id;
	private final String backend;
	private final StandardToolExecutionEnvironment environment;
	private final Supplier<List<String>> commandResolver;
	private final ToolCommandLineBuilder commandLineBuilder;

	private volatile boolean resolved;
	private List<String> command = Collections.emptyList();
	private String resolutionFailure;

	public StandardExternalTool(String id, String backend, StandardToolExecutionEnvironment environment, Supplier<List<String>> commandResolver) {
		this(id, backend, environment, commandResolver, null);
	}

	public StandardExternalTool(String id, String backend, StandardToolExecutionEnvironment environment, Supplier<List<String>> commandResolver,
			ToolCommandLineBuilder commandLineBuilder) {
		this.id = id;
		this.backend = backend;
		this.environment = environment;
		this.commandResolver = commandResolver;
		this.commandLineBuilder = commandLineBuilder;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public ToolAvailability availability() {
		resolve();
		return command.isEmpty() ? ToolAvailability.UNAVAILABLE : ToolAvailability.AVAILABLE;
	}

	@Override
	public ToolDescriptor descriptor() {
		resolve();
		return new ToolDescriptor(id, backend, command, availability(), resolutionFailure);
	}

	@Override
	public ToolExecution openExecution() {
		if (availability() != ToolAvailability.AVAILABLE)
			throw new IllegalStateException("External tool '" + id + "' is unavailable: " + resolutionFailure);
		return new StandardToolExecution(this, environment.openScope());
	}

	StandardToolExecutionEnvironment environment() {
		return environment;
	}

	ToolExecutionResult execute(StandardToolExecutionScope scope, ToolExecutionRequest request) throws Exception {
		resolve();
		if (command.isEmpty())
			throw new IllegalStateException("External tool '" + id + "' is unavailable: " + resolutionFailure);

		List<String> commandParts;
		if (commandLineBuilder == null) {
			commandParts = new ArrayList<String>(command.size() + request.getArguments().size());
			commandParts.addAll(command);
			commandParts.addAll(request.getArguments());
		} else {
			commandParts = commandLineBuilder.build(scope.workspace(), request.getArguments());
		}
		if (commandParts == null || commandParts.isEmpty())
			throw new IllegalStateException("Command line builder returned no command for external tool '" + id + "'.");

		RunCommandRequest lowLevelRequest = RunCommandRequest.builder() //
				.command(commandParts.toArray(new String[commandParts.size()])) //
				.timeout(request.getTimeout()) //
				.retries(request.getRetries()) //
				.retryDelay(request.getRetryDelay()) //
				.env(request.getEnvironmentVariables()) //
				.input(request.getInput()) //
				.silent(request.isSilent()) //
				.workingDirectory(scope.workspace().root().toFile()) //
				.build();

		RunCommandContext result = environment.commandExecution().runCommand(lowLevelRequest);
		return new ToolExecutionResult(result.getErrorCode(), result.getOutput(), result.getError());
	}

	private void resolve() {
		if (resolved)
			return;

		synchronized (this) {
			if (resolved)
				return;
			try {
				List<String> resolvedCommand = commandResolver.get();
				if (resolvedCommand == null || resolvedCommand.isEmpty()) {
					resolutionFailure = "No command was resolved.";
				} else {
					command = Collections.unmodifiableList(new ArrayList<String>(resolvedCommand));
				}
			} catch (Exception e) {
				resolutionFailure = e.getMessage() == null ? e.getClass().getName() : e.getMessage();
			} finally {
				resolved = true;
			}
		}
	}
}
