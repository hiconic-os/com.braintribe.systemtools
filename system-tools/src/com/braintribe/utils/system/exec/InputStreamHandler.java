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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import com.braintribe.logging.Logger;

/**
 * This class is mainly used when an external process is started in Java. Its main purpose is to buffer the output and
 * error streams of an external process started from within the VM.
 * 
 * see http://hacks.oreilly.com/pub/h/1092
 * 
 * @author roman.kurmanowytsch
 * @deprecated There are some cases where the streams are simply blocked. Switched to file output.
 */
@Deprecated
public class InputStreamHandler extends Thread {
	private static Logger logger = Logger.getLogger(InputStreamHandler.class);

	public static String newline = System.getProperty("line.separator");

	/**
	 * Stream being read
	 */
	protected final InputStream stream;

	protected volatile boolean bufferReady = false;

	/**
	 * The StringBuffer holding the captured output
	 */
	protected final StringBuffer stringBuffer;

	public InputStreamHandler(StringBuffer captureBuffer, InputStream stream) {
		this.stream = stream;
		this.stringBuffer = captureBuffer;
	}

	/**
	 * Stream the data.
	 */

	@Override
	public void run() {
		Writer writer = new StringWriter();
		try {
			readCharacters(writer);
			this.stringBuffer.append(writer.toString());
		} catch (IOException ioe) {
			// ignore
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				logger.warn("Could not close StringWriter ");
			}
			this.bufferReady = true;
		}
	}

	protected void readCharacters(Writer writer) throws IOException {
		Reader br = new InputStreamReader(stream);
		try {
			char[] buffer = new char[4096];
			int n = 0;
			while (-1 != (n = br.read(buffer))) {
				writer.write(buffer, 0, n);
			}
		} finally {
			br.close();
		}
	}

	/**
	 * @return True, when the process has finished writing.
	 */
	public boolean isBufferReady() {
		return bufferReady;
	}

	public static String getBuildVersion() {
		return "$Build_Version$ $Id: InputStreamHandler.java 86406 2015-05-28 14:39:44Z roman.kurmanowytsch $";
	}
}
