package de.blafoo.growatt.md5;

import java.net.MalformedURLException;
import java.net.URL;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openjdk.nashorn.api.scripting.URLReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MD5 {

	public static String md5(String password) {
		
		try {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
			
			engine.eval(new URLReader(new URL("https://server.growatt.com/javaScript/xhb/js/MD5.js?1.0.0")));

			Invocable inv = (Invocable) engine;
			return (String) inv.invokeFunction("MD5", password);
		} catch (NoSuchMethodException | ScriptException | MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
}
