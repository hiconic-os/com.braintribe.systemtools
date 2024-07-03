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
package com.braintribe.utils.system;


import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

/**
 * Provides functionality for approximating the CPU load generated by this Java Virtual Machine.
 * Due to the nature of the querying, the load generated by the current thread is not included in the calculation.
 */
public class JvmCpuLoad {
	RuntimeMXBean rmxb = ManagementFactory.getRuntimeMXBean();
	ThreadMXBean tmb = ManagementFactory.getThreadMXBean();
	OperatingSystemMXBean osmb = ManagementFactory.getOperatingSystemMXBean();
	
	/*
	 * This example shows how the load of the current thread is not included in the CPU load computation 
	 */
//	public static void main(String[] args) {
//		while(true) {
//			for (int i=0; i < Integer.MAX_VALUE; i++) {
//				double a = 987654321 * 1234567890 * 987654321;
//			}
//			System.out.println(
//				String.format(
//					"%.5f",
//					new JvmCpuLoad().calculateCpuUsagePercent()
//				)
//			);
//			
//		}
//	}
	
	/**
	 * Approximates the CPU load by comparing the elapsed time with the
	 * CpuTime used by all threads combined.<br/>
	 * @return an approximation of the CPU load generated by this JVM 
	 */
	public float calculateCpuUsagePercent() {
		long beforeCpuTime = calculateTotalCpuTime();
		long beforeUptime = rmxb.getUptime();

		// Wait a while
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) { /* ignore */
		}

		long afterCpuTime = calculateTotalCpuTime();
		long afterUptime = rmxb.getUptime();

		long elapsedCpu = afterCpuTime - beforeCpuTime;
		long elapsedTime = afterUptime - beforeUptime;
		int nCPUs = osmb.getAvailableProcessors();

		/* CPU Usage could go higher than 100%
		 * because elapsedTime and elapsedCpu are not fetched simultaneously.
		 */
		float cpuUsage = Math.min(100F, elapsedCpu / (elapsedTime * 10000F * nCPUs));
		return cpuUsage;
	}
	
	private long calculateTotalCpuTime() {
		long cpuTime = 0;
		for (long threadId : tmb.getAllThreadIds())
			cpuTime += tmb.getThreadCpuTime(threadId);

		return cpuTime;
	}
}
