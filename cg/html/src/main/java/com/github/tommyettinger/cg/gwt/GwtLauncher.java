package com.github.tommyettinger.cg.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.github.tommyettinger.cg.Main;
import com.github.tommyettinger.cg.MainOld;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
		@Override
		public GwtApplicationConfiguration getConfig () {
			// Resizable application, uses available space in browser with no padding:
//			GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
//			cfg.padVertical = 120;
//			cfg.padHorizontal = 0;
//			return cfg;
			// If you want a fixed size application, comment out the above resizable section,
			// and uncomment below:
			GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(960, 480);
			return cfg;
		}

		@Override
		public ApplicationListener createApplicationListener () {
			return new MainOld();
		}
}
