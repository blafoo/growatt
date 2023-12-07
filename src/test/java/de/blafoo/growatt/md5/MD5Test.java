package de.blafoo.growatt.md5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MD5Test {
	
	@Test
	void testMD5() {
		assertEquals("bc8c2fa9a9d734eb030b44e97c75f7ce", MD5.md5("PlainPassword"));
	}

}
