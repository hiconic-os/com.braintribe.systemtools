package com.braintribe.utils.system.exec.tool;

public class ToolExecutionResult {

	private final int exitCode;
	private final String standardOutput;
	private final String standardError;

	public ToolExecutionResult(int exitCode, String standardOutput, String standardError) {
		this.exitCode = exitCode;
		this.standardOutput = standardOutput;
		this.standardError = standardError;
	}

	public int getExitCode() {
		return exitCode;
	}

	public String getStandardOutput() {
		return standardOutput;
	}

	public String getStandardError() {
		return standardError;
	}

	public boolean isSuccessful() {
		return exitCode == 0;
	}
}
