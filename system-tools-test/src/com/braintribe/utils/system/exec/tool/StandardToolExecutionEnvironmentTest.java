package com.braintribe.utils.system.exec.tool;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.braintribe.utils.system.exec.CommandExecution;
import com.braintribe.utils.system.exec.RunCommandContext;
import com.braintribe.utils.system.exec.RunCommandRequest;

public class StandardToolExecutionEnvironmentTest {

	private Path root;
	private CapturingCommandExecution commandExecution;
	private StandardToolExecutionEnvironment environment;

	@Before
	public void initialize() throws Exception {
		root = Files.createTempDirectory("external-tools-test");
		commandExecution = new CapturingCommandExecution();
		environment = new StandardToolExecutionEnvironment(commandExecution, root, "test");
	}

	@After
	public void cleanup() throws Exception {
		deleteRecursively(root);
	}

	@Test
	public void boundExecutionPrefixesCommandAndCleansWorkspace() throws Exception {
		ExternalTool tool = environment.register("echo", "echo-command");
		Path workspaceRoot;

		try (ToolExecution execution = tool.openExecution()) {
			workspaceRoot = execution.workspace().root();
			Files.write(execution.workspace().resolve("input.txt"), "content".getBytes(StandardCharsets.UTF_8));

			ToolExecutionResult result = execution.execute(ToolExecutionRequest.builder().argument("hello").timeout(1000L).build());

			Assert.assertTrue(result.isSuccessful());
			Assert.assertArrayEquals(new String[] { "echo-command", "hello" }, commandExecution.lastRequest.getCommandParts());
			Assert.assertEquals(workspaceRoot.toFile(), commandExecution.lastRequest.getWorkingDirectory());
			Assert.assertTrue(Files.exists(workspaceRoot));
		}

		Assert.assertFalse(Files.exists(workspaceRoot));
	}

	@Test
	public void oneScopeCanExecuteMultipleTools() throws Exception {
		ExternalTool first = environment.register("first", "first-command");
		ExternalTool second = environment.register("second", "second-command");

		try (ToolExecutionScope scope = environment.openScope()) {
			scope.execute(first, ToolExecutionRequest.builder().argument("one").build());
			scope.execute(second, ToolExecutionRequest.builder().argument("two").build());
		}

		Assert.assertEquals(2, commandExecution.requests.size());
		Assert.assertArrayEquals(new String[] { "first-command", "one" }, commandExecution.requests.get(0).getCommandParts());
		Assert.assertArrayEquals(new String[] { "second-command", "two" }, commandExecution.requests.get(1).getCommandParts());
	}

	@Test
	public void commandLineBuilderCanUseWorkspace() throws Exception {
		environment.register("containerized", () -> java.util.Collections.singletonList("containerized"), (workspace, arguments) -> {
			List<String> command = new ArrayList<String>();
			command.add("container");
			command.add("--workdir");
			command.add(workspace.root().toString());
			command.add("containerized");
			command.addAll(arguments);
			return command;
		});

		try (ToolExecution execution = environment.required("containerized").openExecution()) {
			execution.execute(ToolExecutionRequest.builder().argument("input.pdf").build());
			Assert.assertArrayEquals(
					new String[] { "container", "--workdir", execution.workspace().root().toString(), "containerized", "input.pdf" },
					commandExecution.lastRequest.getCommandParts());
		}
	}

	@Test
	public void externalSpaceOutlivesExecutionScope() throws Exception {
		Path externalFile = environment.externalSpace("models").path().resolve("model.bin");
		Files.write(externalFile, new byte[] { 1, 2, 3 });

		try (ToolExecutionScope scope = environment.openScope()) {
			Files.write(scope.workspace().resolve("temporary.bin"), new byte[] { 4, 5, 6 });
		}

		Assert.assertTrue(Files.exists(externalFile));
	}

	@Test(expected = IllegalArgumentException.class)
	public void workspaceRejectsTraversal() {
		try (ToolExecutionScope scope = environment.openScope()) {
			scope.workspace().resolve("../outside");
		}
	}

	@Test(expected = IllegalStateException.class)
	public void missingRequiredToolFailsEarly() {
		environment.required("missing");
	}

	@Test(expected = IllegalStateException.class)
	public void duplicateToolIdsAreRejected() {
		environment.register("duplicate", "first");
		environment.register("duplicate", "second");
	}

	private void deleteRecursively(Path path) throws Exception {
		if (path == null || !Files.exists(path))
			return;
		try (java.util.stream.Stream<Path> paths = Files.walk(path)) {
			paths.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
				try {
					Files.deleteIfExists(p);
				} catch (Exception ignored) {
					// Test cleanup must not hide the test result.
				}
			});
		}
	}

	private static class CapturingCommandExecution implements CommandExecution {
		private final List<RunCommandRequest> requests = new ArrayList<RunCommandRequest>();
		private RunCommandRequest lastRequest;

		@Override
		public RunCommandContext runCommand(RunCommandRequest request) {
			lastRequest = request;
			requests.add(request);
			return new RunCommandContext(0, "ok", "");
		}
	}
}
