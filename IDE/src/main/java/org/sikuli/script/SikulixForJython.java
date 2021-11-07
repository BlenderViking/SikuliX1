/*
 * Copyright (c) 2010-2021, sikuli.org, sikulix.com - MIT license
 */
package org.sikuli.script;

import java.io.File;
import java.net.URL;

import org.sikuli.script.runnerSupport.JythonSupport;
import org.sikuli.script.support.Commons;
import org.sikuli.script.support.RunTime;

/**
 * Can be used in pure Jython environments to add the Sikuli Python API to sys.path<br>
 * Usage: (before any Sikuli features are used)<br>
 * import org.sikuli.script.SikulixForJython<br>
 * from sikuli import *
 */
public class SikulixForJython {

  private static SikulixForJython instance = null;
  private static final int lvl = 3;

  static {
    init();
  }

  private SikulixForJython() {
    //init();
  }

  public static SikulixForJython get() {
    if (null == instance) {
      instance = new SikulixForJython();
    }
    return instance;
  }

  static void init() {
    JythonSupport helper = JythonSupport.get();
    helper.log(lvl, "SikulixForJython: init: starting");
    if (null == helper.existsSysPathModule("sikuli/Sikuli")) {
      URL urlSX = RunTime.resourceLocation("/Lib/sikuli/Sikuli.py");
      if (null == urlSX) {
        throw new SikuliXception(String.format("fatal: " + "Jython: " + "no suitable sikulix...jar on classpath"));
      }
      String pathSX = Commons.asFile(urlSX, "").getParentFile().getParent();
      helper.log(0, "***** %s", pathSX);
      helper.addSysPath(pathSX);
/*
      String fpSikuliStuff = Commons.getLibFolder().getAbsolutePath();
      if (!helper.hasSysPath(fpSikuliStuff)) {
        helper.log(lvl, "sikuli/*.py not found on current Jython::sys.path");
        helper.addSysPath(fpSikuliStuff);
        if (!helper.hasSysPath(fpSikuliStuff)) {
          //helper.terminate(999, "not possible to add to Jython::sys.path: %s", fpSikuliStuff);
          throw new SikuliXception(String.format("fatal: " + "Jython: " +
                  "not possible to add to Jython::sys.path: %s", fpSikuliStuff));
        }
        helper.log(lvl, "added as Jython::sys.path[0]:\n%s", fpSikuliStuff);
      } else {
        helper.log(lvl, "sikuli/*.py is on Jython::sys.path at:\n%s", fpSikuliStuff);
      }
*/
    }
    helper.addSitePackages();
//TODO default ImagePath???
/*
    List<String> sysArgv = helper.getSysArgv();
    if (sysArgv.size() > 0) {
      String path = sysArgv.get(0);
      Settings.BundlePath = new File(path).getParent();
      helper.log(lvl, "Default ImagePath: %s", Settings.BundlePath);
    }
*/
    helper.log(lvl, "SikulixForJython: init: success");
  }
}
