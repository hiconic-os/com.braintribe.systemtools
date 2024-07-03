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
package com.braintribe.utils.system.info;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.utils.system.SystemTools;

public class SystemInformationCollectorImpl implements SystemInformationCollector {
	
	public static String lineSeparator = System.getProperty("line.separator");

	protected SystemTools systemTools = null;
	
	@Override
	public String collectSystemInformation() {

		StringBuilder sb = new StringBuilder();

		String os = SystemTools.getOperatingSystem();
		sb.append("Operating System: ");
		sb.append(os);
		sb.append(lineSeparator);

		int availableProcessors = SystemTools.getAvailableProcessors();
		sb.append("Available Processors: ");
		sb.append(availableProcessors);
		sb.append(lineSeparator);

		String detailedProcessorInformation = this.systemTools.getDetailedProcessorInformation();
		if ((detailedProcessorInformation != null) && (detailedProcessorInformation.trim().length() > 0)) {
			sb.append("Detailed Processor Information:");
			sb.append(lineSeparator);
			sb.append(detailedProcessorInformation);
			sb.append(lineSeparator);
		}

		long freeMemory = SystemTools.getFreeMemory();
		long totalMemory = SystemTools.getTotalMemory();
		sb.append("Free Memory: ");
		sb.append(SystemTools.prettyPrintBytes(freeMemory));
		sb.append(" (");
		sb.append(freeMemory);
		sb.append(')');
		sb.append(lineSeparator);

		sb.append("Total Memory: ");
		sb.append(SystemTools.prettyPrintBytes(totalMemory));
		sb.append(" (");
		sb.append(totalMemory);
		sb.append(')');
		sb.append(lineSeparator);

		String fileSystemInformation = SystemTools.getFileSystemInformation();
		sb.append("File Systems:");
		sb.append(lineSeparator);
		sb.append(fileSystemInformation);
		sb.append(lineSeparator);

		return sb.toString();
	}

	@Required
	@Configurable
	public void setSystemTools(SystemTools systemTools) {
		this.systemTools = systemTools;
	}


}
