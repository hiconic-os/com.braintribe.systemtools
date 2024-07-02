// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.utils.system.exec;


public class RunCommandContext {
	
	protected int errorCode = 0;
	protected String output;
	protected String error;
	
	public RunCommandContext(int errorCode, String output, String error) {
		this.errorCode = errorCode;
		this.output = output;
		this.error = error;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public String getOutput() {
		return output;
	}
	
	public String getError() {
		return error;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		
		buf.append("exit code: ");
		buf.append(this.errorCode);
		
		if ((this.output != null) && (this.output.trim().length() > 0)) {
			buf.append("\nstdout:\n");
			buf.append(this.output.trim());
		} 

		if ((this.error != null) && (this.error.trim().length() > 0)) {
			buf.append("\nstderr:\n");
			buf.append(this.error.trim());
		} 
		
		return buf.toString();
	}

	public static String getBuildVersion() {
		return "$Build_Version$ $Id: RunCommandContext.java 86406 2015-05-28 14:39:44Z roman.kurmanowytsch $";
	}
}
