package com.braintribe.utils.system.exec.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class ToolExecutionRequest {

	private final List<String> arguments;
	private final long timeout;
	private final int retries;
	private final long retryDelay;
	private final Map<String, String> environmentVariables;
	private final boolean silent;
	private final String input;

	private ToolExecutionRequest(Builder builder) {
		this.arguments = Collections.unmodifiableList(new ArrayList<String>(builder.arguments));
		this.timeout = builder.timeout;
		this.retries = builder.retries;
		this.retryDelay = builder.retryDelay;
		this.environmentVariables = builder.environmentVariables == null ? null
				: Collections.unmodifiableMap(new LinkedHashMap<String, String>(builder.environmentVariables));
		this.silent = builder.silent;
		this.input = builder.input;
	}

	public static Builder builder() {
		return new Builder();
	}

	public List<String> getArguments() {
		return arguments;
	}

	public long getTimeout() {
		return timeout;
	}

	public int getRetries() {
		return retries;
	}

	public long getRetryDelay() {
		return retryDelay;
	}

	public Map<String, String> getEnvironmentVariables() {
		return environmentVariables;
	}

	public boolean isSilent() {
		return silent;
	}

	public String getInput() {
		return input;
	}

	public static class Builder {
		private final List<String> arguments = new ArrayList<String>();
		private long timeout = -1L;
		private int retries = -1;
		private long retryDelay = -1L;
		private Map<String, String> environmentVariables;
		private boolean silent;
		private String input;

		public Builder argument(String argument) {
			arguments.add(argument);
			return this;
		}

		public Builder arguments(String... arguments) {
			if (arguments != null)
				Collections.addAll(this.arguments, arguments);
			return this;
		}

		public Builder arguments(List<String> arguments) {
			if (arguments != null)
				this.arguments.addAll(arguments);
			return this;
		}

		public Builder timeout(long timeout) {
			this.timeout = timeout;
			return this;
		}

		public Builder retries(int retries) {
			this.retries = retries;
			return this;
		}

		public Builder retryDelay(long retryDelay) {
			this.retryDelay = retryDelay;
			return this;
		}

		public Builder environmentVariables(Map<String, String> environmentVariables) {
			this.environmentVariables = environmentVariables;
			return this;
		}

		public Builder silent(boolean silent) {
			this.silent = silent;
			return this;
		}

		public Builder input(String input) {
			this.input = input;
			return this;
		}

		public ToolExecutionRequest build() {
			return new ToolExecutionRequest(this);
		}
	}
}
