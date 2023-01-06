package com.github.tommyettinger;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import de.damios.guacamole.gdx.graphics.NestableFrameBuffer;
import de.eskalon.commons.core.ManagedGame;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.utils.BasicInputMultiplexer;
import com.github.tommyettinger.screens.BaseScreen;
import com.github.tommyettinger.screens.TitleScreen;
import com.github.tommyettinger.screens.TransitionManager;
import com.github.tommyettinger.utils.Time;
import com.github.tommyettinger.utils.accessors.*;

public class Main extends ManagedGame<BaseScreen, ScreenTransition> {

	public static Main game;

	public Assets assets;
	public Engine engine;
	public TweenManager tween;
	public NestableFrameBuffer frameBuffer;
	public TextureRegion frameBufferRegion;
	public OrthographicCamera windowCamera;

	public Main() {
		Main.game = this;
	}

	@Override
	public void create() {
		Time.init();
//		LongLongBiConsumer funderbyThing = (a, b) -> a ^ ~b;
		assets = new Assets();

		VisUI.load(assets.mgr.get("ui/uiskin.json", Skin.class));
		{
			Skin skin = VisUI.getSkin();
			skin.getFont("default")            .getData().markupEnabled = true;
			skin.getFont("font")               .getData().markupEnabled = true;
			skin.getFont("list")               .getData().markupEnabled = true;
			skin.getFont("subtitle")           .getData().markupEnabled = true;
			skin.getFont("window")             .getData().markupEnabled = true;
			skin.getFont("outfit-medium-20px") .getData().markupEnabled = true;
			skin.getFont("outfit-medium-40px") .getData().markupEnabled = true;
			skin.getFont("outfit-medium-80px") .getData().markupEnabled = true;
		}

		engine = new Engine();

		tween = new TweenManager();
		{
			Tween.setWaypointsLimit(4);
			Tween.setCombinedAttributesLimit(4);
			Tween.registerAccessor(Color.class, new ColorAccessor());
			Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
			Tween.registerAccessor(Vector2.class, new Vector2Accessor());
			Tween.registerAccessor(Vector3.class, new Vector3Accessor());
			Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
		};

		Pixmap.Format format = Pixmap.Format.RGBA8888;
		int width = Config.Screen.framebuffer_width;
		int height = Config.Screen.framebuffer_height;
		boolean hasDepth = true;
		frameBuffer = new NestableFrameBuffer(format, width, height, hasDepth);
		Texture frameBufferTexture = frameBuffer.getColorBufferTexture();
		frameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		frameBufferRegion = new TextureRegion(frameBufferTexture);
		frameBufferRegion.flip(false, true);

		windowCamera = new OrthographicCamera();
		windowCamera.setToOrtho(false, Config.Screen.window_width, Config.Screen.window_height);
		windowCamera.update();

		BasicInputMultiplexer inputMux = new BasicInputMultiplexer();
		Gdx.input.setInputProcessor(inputMux);

		screenManager.initialize(inputMux, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		screenManager.addScreen("title", new TitleScreen());
		TransitionManager.initialize(screenManager);

		screenManager.pushScreen("title", TransitionManager.TransitionType.BLEND.name());
	}


	public void update(float delta) {
		// handle top level input
		{
			if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
				Gdx.app.exit();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) Config.Debug.general = !Config.Debug.general;
		}

		// update things that must update every tick
		{
			Time.update();
			tween.update(Time.delta);
		}

		// handle a pause
		{
			if (Time.pause_timer > 0) {
				Time.pause_timer -= Time.delta;
				if (Time.pause_timer <= -0.0001f) {
					Time.delta = -Time.pause_timer;
				} else {
					// skip updates if we're paused
					return;
				}
			}
			Time.millis += Time.delta;
			Time.previous_elapsed = Time.elapsed_millis();
		}

		// update systems
		{
			// TODO - need a way to separate 'pausable update' from 'always update' on entity components
			engine.update(Time.delta);
		}
	}

	@Override
	public void render() {
		update(Time.delta);
		screenManager.render(Time.delta);
	}

	@Override
	public void dispose() {
		screenManager.getScreens().forEach(BaseScreen::dispose);
		frameBuffer.dispose();
		assets.dispose();
	}

}
