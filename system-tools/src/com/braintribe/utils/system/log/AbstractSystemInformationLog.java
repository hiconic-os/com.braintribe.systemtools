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
package com.braintribe.utils.system.log;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.DestructionAware;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.logging.Logger.LogLevel;
import com.braintribe.utils.system.info.SystemInformationCollector;

public abstract class AbstractSystemInformationLog implements Runnable, DestructionAware {

	private static Logger logger = Logger.getLogger(AbstractSystemInformationLog.class);
	
	protected LogLevel logLevel = LogLevel.DEBUG;
	protected boolean stopProcessing = false;
	protected SystemInformationCollector systemInformationCollector = null;
	
	@Override
	public void run() {
		
		try {
			String info = this.systemInformationCollector.collectSystemInformation();
			this.logInformation(info);
		} catch(Exception e) {
			logger.error("Could not write system information to log", e);
		}
		
	}

	protected abstract void logInformation(String info);

	@Override
	public void preDestroy() {
		this.stopProcessing = true;
	}
	
	public LogLevel getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	@Configurable
	@Required
	public void setSystemInformationCollector(SystemInformationCollector systemInformationCollector) {
		this.systemInformationCollector = systemInformationCollector;
	}

}
