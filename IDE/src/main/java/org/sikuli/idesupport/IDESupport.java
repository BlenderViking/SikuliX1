/*
 * Copyright (c) 2010-2021, sikuli.org, sikulix.com - MIT license
 */
package org.sikuli.idesupport;

import org.sikuli.basics.Debug;
import org.sikuli.basics.FileManager;
import org.sikuli.ide.SikulixIDE;
import org.sikuli.script.Sikulix;
import org.sikuli.script.runnerSupport.IScriptRunner;
import org.sikuli.script.runners.*;
import org.sikuli.script.runnerSupport.Runner;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class IDESupport {

	private static final String me = "IDESupport: ";
	private static final int lvl = 3;
	private static void log(int level, String message, Object... args) {
		Debug.logx(level, me + message, args);
	}

	public static Map<String, IIDESupport> ideSupporter = new HashMap<String, IIDESupport>();

	public static void initIDESupport() {
		ServiceLoader<IIDESupport> sloader = ServiceLoader.load(IIDESupport.class);
		Iterator<IIDESupport> supIterator = sloader.iterator();
		while (supIterator.hasNext()) {
			IIDESupport current = supIterator.next();
			try {
				for (String ending : current.getTypes()) {
					ideSupporter.put(ending, current);
				}
			} catch (Exception ex) {
			}
		}
	}

	private static final Class<?>[] IDE_RUNNER_CLASSES = new Class<?>[]{
			JythonRunner.class,
			PythonRunner.class,
			JRubyRunner.class,
//			JavaScriptRunner.class,
			TextRunner.class};

	private static final List<IScriptRunner> IDE_RUNNERS = new ArrayList<>();

	public static boolean transferScript(String src, String dest, IScriptRunner runner) {
		FileManager.FileFilter filter = new FileManager.FileFilter() {
			@Override
			public boolean accept(File entry) {
				if (entry.getName().endsWith(".html")) {
					return false;
				} else if (entry.getName().endsWith(".$py.class")) {
					return false;
				} else {
					for (String ending : runner.getExtensions()) {
						if (entry.getName().endsWith("." + ending)) {
							return false;
						}
					}
				}
				return true;
			}
		};
		try {
			FileManager.xcopy(src, dest, filter);
		} catch (IOException ex) {
			log(-1, "transferScript: %s", ex.getMessage());
			return false;
		}
		return true;
	}

	public static void init() {
		synchronized(IDE_RUNNERS) {
			if(IDE_RUNNERS.isEmpty()) {
				log(lvl, "enter");

				List<IScriptRunner> runners = Runner.getRunners();

				for (Class<?> runnerClass : IDE_RUNNER_CLASSES) {
					for (IScriptRunner runner : runners) {
						if(runnerClass.equals(runner.getClass())) {
							log(lvl, "added: %s", runner.getName());
							IDE_RUNNERS.add(runner);
							break;
						}
					}
				}

				if (IDE_RUNNERS.isEmpty()) {
					String em = "Terminating: No scripting support available. Rerun Setup!";
					log(-1, em);
					Sikulix.popError(em, "IDE has problems ...");
					System.exit(1);
				}

				defaultRunner = IDE_RUNNERS.get(0);
				log(lvl, "exit: defaultrunner: %s (%s)", defaultRunner.getName(), defaultRunner.getExtensions()[0]);
				initIDESupport();
			}
		}
	}

	public static IScriptRunner getDefaultRunner() {
		return defaultRunner;
	}

	private static IScriptRunner defaultRunner = null;

	public static synchronized List<IScriptRunner> getRunners(){
		synchronized(IDE_RUNNERS) {
			init();
			return new ArrayList<IScriptRunner>(IDE_RUNNERS);
		}
	}

	public static ImageIcon getIconResource(String name) {
		URL url = SikulixIDE.class.getResource(name);
		if (url == null) {
			Debug.error("Warning: could not load \"" + name + "\" icon");
			return null;
		}
		return new ImageIcon(url);
	}
}
