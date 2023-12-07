/**
 * 1。密码
	
   2、将字符串编码为utf-8。 str2rstr_utf8

 * 
 */
/**
 * 1、输入密码字符串
 * 2、将字符串编码为utf-8。 str2rstr_utf8
 * 3、计算原始字符串的MD5。 rstr_md5
 * 4、将原始字符串转换为十六进制字符串 rstr2hex
 * */


function MD5(instring) {
	var hexcase = 0; /* hex output format. 0 - lowercase; 1 - uppercase        */
	var b64pad = ""; /* base-64 pad character. "=" for strict RFC compliance   */

	/*
	 * 这些是您通常想要调用的函数
	 * 它们接受字符串参数并返回十六进制或 base-64 编码的字符串
	 */
	function hex_md5(s) {
		return rstr2hex(rstr_md5(str2rstr_utf8(s)));
	}

	function b64_md5(s) {
		return rstr2b64(rstr_md5(str2rstr_utf8(s)));
	}

	function any_md5(s, e) {
		return rstr2any(rstr_md5(str2rstr_utf8(s)), e);
	}

	function hex_hmac_md5(k, d) {
		return rstr2hex(rstr_hmac_md5(str2rstr_utf8(k), str2rstr_utf8(d)));
	}

	function b64_hmac_md5(k, d) {
		return rstr2b64(rstr_hmac_md5(str2rstr_utf8(k), str2rstr_utf8(d)));
	}

	function any_hmac_md5(k, d, e) {
		return rstr2any(rstr_hmac_md5(str2rstr_utf8(k), str2rstr_utf8(d)), e);
	}

	/*
	 * 执行简单的自检以查看虚拟机是否正常工作
	 */
	function md5_vm_test() {
		return hex_md5("abc").toLowerCase() == "900150983cd24fb0d6963f7d28e17f72";
	}

	/*
	 * 计算原始字符串的MD5
	 */
	function rstr_md5(s) {
		return binl2rstr(binl_md5(rstr2binl(s), s.length * 8));
	}

	/*
	 * 计算密钥和一些数据（原始字符串）的 HMAC-MD5
	 */
	function rstr_hmac_md5(key, data) {
		var bkey = rstr2binl(key);
		if (bkey.length > 16) bkey = binl_md5(bkey, key.length * 8);

		var ipad = Array(16),
			opad = Array(16);
		for (var i = 0; i < 16; i++) {
			ipad[i] = bkey[i] ^ 0x36363636;
			opad[i] = bkey[i] ^ 0x5C5C5C5C;
		}

		var hash = binl_md5(ipad.concat(rstr2binl(data)), 512 + data.length * 8);
		return binl2rstr(binl_md5(opad.concat(hash), 512 + 128));
	}

	/*
	 * 将原始字符串转换为十六进制字符串
	 */
	function rstr2hex(input) {
		try {
			hexcase
		} catch (e) {
			hexcase = 0;
		}
		var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
		var output = "";
		var x;
		for (var i = 0; i < input.length; i++) {
			x = input.charCodeAt(i);
			output += hex_tab.charAt((x >>> 4) & 0x0F) +
				hex_tab.charAt(x & 0x0F);
		}
		return output;
	}

	/*
	 * 将原始字符串转换为 Base-64 字符串
	 */
	function rstr2b64(input) {
		try {
			b64pad
		} catch (e) {
			b64pad = '';
		}
		var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		var output = "";
		var len = input.length;
		for (var i = 0; i < len; i += 3) {
			var triplet = (input.charCodeAt(i) << 16) |
				(i + 1 < len ? input.charCodeAt(i + 1) << 8 : 0) |
				(i + 2 < len ? input.charCodeAt(i + 2) : 0);
			for (var j = 0; j < 4; j++) {
				if (i * 8 + j * 6 > input.length * 8) output += b64pad;
				else output += tab.charAt((triplet >>> 6 * (3 - j)) & 0x3F);
			}
		}
		return output;
	}

	/*
	 * 将原始字符串转换为任意字符串编码
	 */
	function rstr2any(input, encoding) {
		var divisor = encoding.length;
		var i, j, q, x, quotient;

		/* Convert to an array of 16-bit big-endian values, forming the dividend */
		var dividend = Array(Math.ceil(input.length / 2));
		for (i = 0; i < dividend.length; i++) {
			dividend[i] = (input.charCodeAt(i * 2) << 8) | input.charCodeAt(i * 2 + 1);
		}

		/*
		 * Repeatedly perform a long division. The binary array forms the dividend,
		 * the length of the encoding is the divisor. Once computed, the quotient
		 * forms the dividend for the next step. All remainders are stored for later
		 * use.
		 */
		var full_length = Math.ceil(input.length * 8 /
			(Math.log(encoding.length) / Math.log(2)));
		var remainders = Array(full_length);
		for (j = 0; j < full_length; j++) {
			quotient = Array();
			x = 0;
			for (i = 0; i < dividend.length; i++) {
				x = (x << 16) + dividend[i];
				q = Math.floor(x / divisor);
				x -= q * divisor;
				if (quotient.length > 0 || q > 0)
					quotient[quotient.length] = q;
			}
			remainders[j] = x;
			dividend = quotient;
		}

		/* Convert the remainders to the output string */
		var output = "";
		for (i = remainders.length - 1; i >= 0; i--)
			output += encoding.charAt(remainders[i]);

		return output;
	}

	/*
	 * 将字符串编码为utf-8。
	 * 为了提高效率，假设输入是有效的 utf-16。
	 */
	function str2rstr_utf8(input) {
		var output = "";
		var i = -1;
		var x, y;

		while (++i < input.length) {
			/* Decode utf-16 surrogate pairs */
			x = input.charCodeAt(i);
			y = i + 1 < input.length ? input.charCodeAt(i + 1) : 0;
			if (0xD800 <= x && x <= 0xDBFF && 0xDC00 <= y && y <= 0xDFFF) {
				x = 0x10000 + ((x & 0x03FF) << 10) + (y & 0x03FF);
				i++;
			}

			/* Encode output as utf-8 */
			if (x <= 0x7F)
				output += String.fromCharCode(x);
			else if (x <= 0x7FF)
				output += String.fromCharCode(0xC0 | ((x >>> 6) & 0x1F),
					0x80 | (x & 0x3F));
			else if (x <= 0xFFFF)
				output += String.fromCharCode(0xE0 | ((x >>> 12) & 0x0F),
					0x80 | ((x >>> 6) & 0x3F),
					0x80 | (x & 0x3F));
			else if (x <= 0x1FFFFF)
				output += String.fromCharCode(0xF0 | ((x >>> 18) & 0x07),
					0x80 | ((x >>> 12) & 0x3F),
					0x80 | ((x >>> 6) & 0x3F),
					0x80 | (x & 0x3F));
		}
		return output;
	}

	/*
	 * 将字符串编码为utf-16
	 */
	function str2rstr_utf16le(input) {
		var output = "";
		for (var i = 0; i < input.length; i++)
			output += String.fromCharCode(input.charCodeAt(i) & 0xFF,
				(input.charCodeAt(i) >>> 8) & 0xFF);
		return output;
	}

	function str2rstr_utf16be(input) {
		var output = "";
		for (var i = 0; i < input.length; i++)
			output += String.fromCharCode((input.charCodeAt(i) >>> 8) & 0xFF,
				input.charCodeAt(i) & 0xFF);
		return output;
	}

	/*
	 * 将原始字符串转换为小端字节数组
	 * >255 个字符的高字节会被默默忽略。
	 */
	function rstr2binl(input) {
		var output = Array(input.length >> 2);
		for (var i = 0; i < output.length; i++)
			output[i] = 0;
		for (var i = 0; i < input.length * 8; i += 8)
			output[i >> 5] |= (input.charCodeAt(i / 8) & 0xFF) << (i % 32);
		return output;
	}

	/*
	 * Convert an array of little-endian words to a string
	 */
	function binl2rstr(input) {
		var output = "";
		for (var i = 0; i < input.length * 32; i += 8)
			output += String.fromCharCode((input[i >> 5] >>> (i % 32)) & 0xFF);
		return output;
	}

	/*
	 * 计算小端字数组的 MD5 和位长度。
	 */
	function binl_md5(x, len) {
		/* append padding */
		x[len >> 5] |= 0x80 << ((len) % 32);
		x[(((len + 64) >>> 9) << 4) + 14] = len;

		var a = 1732584193;
		var b = -271733879;
		var c = -1732584194;
		var d = 271733878;

		for (var i = 0; i < x.length; i += 16) {
			var olda = a;
			var oldb = b;
			var oldc = c;
			var oldd = d;

			a = md5_ff(a, b, c, d, x[i + 0], 7, -680876936);
			d = md5_ff(d, a, b, c, x[i + 1], 12, -389564586);
			c = md5_ff(c, d, a, b, x[i + 2], 17, 606105819);
			b = md5_ff(b, c, d, a, x[i + 3], 22, -1044525330);
			a = md5_ff(a, b, c, d, x[i + 4], 7, -176418897);
			d = md5_ff(d, a, b, c, x[i + 5], 12, 1200080426);
			c = md5_ff(c, d, a, b, x[i + 6], 17, -1473231341);
			b = md5_ff(b, c, d, a, x[i + 7], 22, -45705983);
			a = md5_ff(a, b, c, d, x[i + 8], 7, 1770035416);
			d = md5_ff(d, a, b, c, x[i + 9], 12, -1958414417);
			c = md5_ff(c, d, a, b, x[i + 10], 17, -42063);
			b = md5_ff(b, c, d, a, x[i + 11], 22, -1990404162);
			a = md5_ff(a, b, c, d, x[i + 12], 7, 1804603682);
			d = md5_ff(d, a, b, c, x[i + 13], 12, -40341101);
			c = md5_ff(c, d, a, b, x[i + 14], 17, -1502002290);
			b = md5_ff(b, c, d, a, x[i + 15], 22, 1236535329);

			a = md5_gg(a, b, c, d, x[i + 1], 5, -165796510);
			d = md5_gg(d, a, b, c, x[i + 6], 9, -1069501632);
			c = md5_gg(c, d, a, b, x[i + 11], 14, 643717713);
			b = md5_gg(b, c, d, a, x[i + 0], 20, -373897302);
			a = md5_gg(a, b, c, d, x[i + 5], 5, -701558691);
			d = md5_gg(d, a, b, c, x[i + 10], 9, 38016083);
			c = md5_gg(c, d, a, b, x[i + 15], 14, -660478335);
			b = md5_gg(b, c, d, a, x[i + 4], 20, -405537848);
			a = md5_gg(a, b, c, d, x[i + 9], 5, 568446438);
			d = md5_gg(d, a, b, c, x[i + 14], 9, -1019803690);
			c = md5_gg(c, d, a, b, x[i + 3], 14, -187363961);
			b = md5_gg(b, c, d, a, x[i + 8], 20, 1163531501);
			a = md5_gg(a, b, c, d, x[i + 13], 5, -1444681467);
			d = md5_gg(d, a, b, c, x[i + 2], 9, -51403784);
			c = md5_gg(c, d, a, b, x[i + 7], 14, 1735328473);
			b = md5_gg(b, c, d, a, x[i + 12], 20, -1926607734);

			a = md5_hh(a, b, c, d, x[i + 5], 4, -378558);
			d = md5_hh(d, a, b, c, x[i + 8], 11, -2022574463);
			c = md5_hh(c, d, a, b, x[i + 11], 16, 1839030562);
			b = md5_hh(b, c, d, a, x[i + 14], 23, -35309556);
			a = md5_hh(a, b, c, d, x[i + 1], 4, -1530992060);
			d = md5_hh(d, a, b, c, x[i + 4], 11, 1272893353);
			c = md5_hh(c, d, a, b, x[i + 7], 16, -155497632);
			b = md5_hh(b, c, d, a, x[i + 10], 23, -1094730640);
			a = md5_hh(a, b, c, d, x[i + 13], 4, 681279174);
			d = md5_hh(d, a, b, c, x[i + 0], 11, -358537222);
			c = md5_hh(c, d, a, b, x[i + 3], 16, -722521979);
			b = md5_hh(b, c, d, a, x[i + 6], 23, 76029189);
			a = md5_hh(a, b, c, d, x[i + 9], 4, -640364487);
			d = md5_hh(d, a, b, c, x[i + 12], 11, -421815835);
			c = md5_hh(c, d, a, b, x[i + 15], 16, 530742520);
			b = md5_hh(b, c, d, a, x[i + 2], 23, -995338651);

			a = md5_ii(a, b, c, d, x[i + 0], 6, -198630844);
			d = md5_ii(d, a, b, c, x[i + 7], 10, 1126891415);
			c = md5_ii(c, d, a, b, x[i + 14], 15, -1416354905);
			b = md5_ii(b, c, d, a, x[i + 5], 21, -57434055);
			a = md5_ii(a, b, c, d, x[i + 12], 6, 1700485571);
			d = md5_ii(d, a, b, c, x[i + 3], 10, -1894986606);
			c = md5_ii(c, d, a, b, x[i + 10], 15, -1051523);
			b = md5_ii(b, c, d, a, x[i + 1], 21, -2054922799);
			a = md5_ii(a, b, c, d, x[i + 8], 6, 1873313359);
			d = md5_ii(d, a, b, c, x[i + 15], 10, -30611744);
			c = md5_ii(c, d, a, b, x[i + 6], 15, -1560198380);
			b = md5_ii(b, c, d, a, x[i + 13], 21, 1309151649);
			a = md5_ii(a, b, c, d, x[i + 4], 6, -145523070);
			d = md5_ii(d, a, b, c, x[i + 11], 10, -1120210379);
			c = md5_ii(c, d, a, b, x[i + 2], 15, 718787259);
			b = md5_ii(b, c, d, a, x[i + 9], 21, -343485551);

			a = safe_add(a, olda);
			b = safe_add(b, oldb);
			c = safe_add(c, oldc);
			d = safe_add(d, oldd);
		}
		return Array(a, b, c, d);
	}

	/*
	 * 这些函数实现了算法使用的四种基本操作
	 */
	function md5_cmn(q, a, b, x, s, t) {
		return safe_add(bit_rol(safe_add(safe_add(a, q), safe_add(x, t)), s), b);
	}

	function md5_ff(a, b, c, d, x, s, t) {
		return md5_cmn((b & c) | ((~b) & d), a, b, x, s, t);
	}

	function md5_gg(a, b, c, d, x, s, t) {
		return md5_cmn((b & d) | (c & (~d)), a, b, x, s, t);
	}

	function md5_hh(a, b, c, d, x, s, t) {
		return md5_cmn(b ^ c ^ d, a, b, x, s, t);
	}

	function md5_ii(a, b, c, d, x, s, t) {
		return md5_cmn(c ^ (b | (~d)), a, b, x, s, t);
	}

	/*
	 * 添加整数，以 2^32 换行。这在内部使用 16 位操作
	 * 解决一些 JS 解释器中的错误。
	 */
	function safe_add(x, y) {
		var lsw = (x & 0xFFFF) + (y & 0xFFFF);
		var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
		return (msw << 16) | (lsw & 0xFFFF);
	}

	/*
	 * 按位左移 32 位数字。
	 */
	function bit_rol(num, cnt) {
		return (num << cnt) | (num >>> (32 - cnt));
	}

	return hex_md5(instring);
}