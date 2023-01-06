package com.github.tommyettinger.screens;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import de.eskalon.commons.screen.ManagedScreen;
import com.github.tommyettinger.Assets;
import com.github.tommyettinger.Config;
import com.github.tommyettinger.Main;
import com.github.tommyettinger.utils.screenshake.ScreenShakeCameraController;

public abstract class BaseScreen extends ManagedScreen implements Disposable {

    public final Main game;
    public final Assets assets;
    public final Engine engine;
    public final TweenManager tween;
    public final SpriteBatch batch;
    public final OrthographicCamera windowCamera;
    public final Vector3 pointerPos;
    public ScreenShakeCameraController screenShaker;
    public Camera worldCamera;

    protected boolean active;

    public BaseScreen() {
        this.game = Main.game;
        this.assets = game.assets;
        this.engine = game.engine;
        this.tween = game.tween;
        this.windowCamera = game.windowCamera;
        this.batch = assets.batch;
        this.pointerPos = new Vector3();
        this.active = false;
    }

    @Override
    protected void create() {
        worldCamera = new OrthographicCamera();
        ((OrthographicCamera) worldCamera).setToOrtho(false, Config.Screen.window_width, Config.Screen.window_height);
        worldCamera.update();
        screenShaker = new ScreenShakeCameraController(worldCamera);
    }

    @Override
    public void show() {
        super.show();
        active = true;
    }

    @Override
    public void hide() {
        active = false;
    }

    public void update(float delta) {
        windowCamera.update();
        if (worldCamera != null) {
            worldCamera.update();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Config.Debug.general = !Config.Debug.general;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            Config.Debug.ui = !Config.Debug.ui;
        }
        screenShaker.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        windowCamera.setToOrtho(false, width, height);
        windowCamera.update();
    }

    @Override
    public void dispose() {

    }

    public void resetWorldCamera() {}

}
