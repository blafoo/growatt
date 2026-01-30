package de.blafoo.growatt.md5;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.nashorn.api.scripting.URLReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public final class MD5 {

	public static String md5(String password) {
		
		try {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
			
			engine.eval(new URLReader(new URI("https://server.growatt.com/javaScript/xhb/js/MD5.js?1.8.6").toURL()));

			Invocable inv = (Invocable) engine;
			return (String) inv.invokeFunction("MD5", password);
		} catch (NoSuchMethodException | ScriptException | MalformedURLException | URISyntaxException e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
}
