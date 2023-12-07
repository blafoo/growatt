package de.blafoo.growatt.md5;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MD5 {

	public static String md5(String password) {
		
		try {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
			
			engine.eval(new InputStreamReader(new ClassPathResource("/MD5.js").getInputStream()));

			Invocable inv = (Invocable) engine;
			return (String) inv.invokeFunction("MD5", password);
		} catch (NoSuchMethodException | ScriptException | IOException e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
}
