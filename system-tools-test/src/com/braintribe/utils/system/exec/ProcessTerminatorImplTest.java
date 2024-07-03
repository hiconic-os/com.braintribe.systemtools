// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.utils.system.exec;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProcessTerminatorImplTest {

	protected ProcessTerminatorImpl impl = null;
	protected ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
	
	@Before
	public void initialize() throws Exception {
		this.impl = new ProcessTerminatorImpl();
		this.service.scheduleWithFixedDelay(this.impl, 0L, 1000L, TimeUnit.MILLISECONDS);
	}
	@After
	public void destroy() throws Exception {
		if (this.service != null) {
			this.service.shutdown();
		}
	}
	
	@Test
	public void testProcessTerminatorImpl() throws Exception {
		
		FakeProcess successProcess = new FakeProcess(true);
		this.impl.addProcess("command", successProcess, 2000L);
		Thread.sleep(4000L);
		Assert.assertEquals(true, successProcess.isExitCalled());
		Assert.assertEquals(false, successProcess.isDestroyCalled());
		
		FakeProcess failureProcess = new FakeProcess(false);
		this.impl.addProcess("command", failureProcess, 2000L);
		Thread.sleep(4000L);
		Assert.assertEquals(true, failureProcess.isExitCalled());
		Assert.assertEquals(true, failureProcess.isDestroyCalled());
		
	}
	
	class FakeProcess extends Process {
		protected boolean exitCalled = false;
		protected boolean destroyCalled = false;
		protected boolean successful = true;
		
		public FakeProcess(boolean successful) {
			this.successful = successful;
		}
		@Override
		public OutputStream getOutputStream() {
			return null;
		}
		@Override
		public InputStream getInputStream() {
			return null;
		}
		@Override
		public InputStream getErrorStream() {
			return null;
		}
		@Override
		public int waitFor() throws InterruptedException {
			return 0;
		}
		@Override
		public int exitValue() {
			this.exitCalled = true;
			if (this.successful) {
				return 0;
			} else {
				throw new IllegalThreadStateException();
			}
		}
		@Override
		public void destroy() {
			this.destroyCalled = true;
		}
		public boolean isExitCalled() {
			return exitCalled;
		}
		public boolean isDestroyCalled() {
			return destroyCalled;
		}		
	}
	
}
