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

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class SystemToolsTest {

	@Test
	public void testCpuCount() {
		assertThat(0).isLessThan(SystemTools.getAvailableProcessors());
	}

	@Test
	public void testMemory() {
		assertThat(0L).isLessThan(SystemTools.getFreeMemory());
		assertThat(0L).isLessThan(SystemTools.getTotalMemory());
	}

	@Test
	public void testOs() {
		String osInformation = SystemTools.getOperatingSystem();
		assertThat(osInformation).isNotNull();
		assertThat(osInformation).isNotEqualTo("");
	}


	@Test
	public void testPrettyPrintFileSizeBinary() {

		assertThat(SystemTools.prettyPrintBytesBinary(0)).isEqualTo("0 B");
		assertThat(SystemTools.prettyPrintBytesBinary(1024)).isEqualTo("1.0 KiB");
		assertThat(SystemTools.prettyPrintBytesBinary(1048576)).isEqualTo("1.0 MiB");
		assertThat(SystemTools.prettyPrintBytesBinary(1048577)).isEqualTo("1.0 MiB");
		assertThat(SystemTools.prettyPrintBytesBinary(512)).isEqualTo("512 B");
		assertThat(SystemTools.prettyPrintBytesBinary(513)).isEqualTo("513 B");
		assertThat(SystemTools.prettyPrintBytesBinary(1023)).isEqualTo("1023 B");
		assertThat(SystemTools.prettyPrintBytesBinary(1200000)).isEqualTo("1.1 MiB");

	}

	@Test
	public void testPrettyPrintFileSizeDecimal() {

		assertThat(SystemTools.prettyPrintBytesDecimal(0)).isEqualTo("0 B");
		assertThat(SystemTools.prettyPrintBytesDecimal(1000)).isEqualTo("1.0 kB");
		assertThat(SystemTools.prettyPrintBytesDecimal(1001)).isEqualTo("1.0 kB");
		assertThat(SystemTools.prettyPrintBytesDecimal(1000000)).isEqualTo("1.0 MB");
		assertThat(SystemTools.prettyPrintBytesDecimal(512)).isEqualTo("512 B");
		assertThat(SystemTools.prettyPrintBytesDecimal(513)).isEqualTo("513 B");
		assertThat(SystemTools.prettyPrintBytesDecimal(1023)).isEqualTo("1.0 kB");
		assertThat(SystemTools.prettyPrintBytesDecimal(1200000)).isEqualTo("1.2 MB");

	}

	@Test
	public void testGetPrettyPrintFreeSpaceOnDiskDevice() throws IOException {
		File tempFile = File.createTempFile("test", ".txt");
		try {
			String freeSpace = SystemTools.getPrettyPrintFreeSpaceOnDiskDevice(tempFile);
			System.out.println(freeSpace);
			assertThat(freeSpace).isNotNull();
		} finally {
			tempFile.delete();
		}

	}
}
