package com.github.tommyettinger.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import de.eskalon.commons.screen.ScreenManager;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import de.eskalon.commons.screen.transition.impl.PushTransition;
import de.eskalon.commons.screen.transition.impl.SlidingDirection;
import com.github.tommyettinger.Assets;
import com.github.tommyettinger.Main;

public class TransitionManager {

    private static final String BUILT_IN_TRANSITION = "built-in";

    public enum TransitionType {
          PAGE_CURL        (0.5f, "shaders/gl-transitions/page-curl.frag")
        , CIRCLE_CROP      (1.5f, "shaders/gl-transitions/circle-crop.frag")
        , DIRECTIONAL_WARP (1.5f, "shaders/gl-transitions/directional-warp.frag")
        , COLOUR_DISTANCE  (1.5f, "shaders/gl-transitions/colour-distance.frag")
        , CROSSHATCH       (1.5f, "shaders/gl-transitions/crosshatch.frag")
        , BLEND            (0.25f, BUILT_IN_TRANSITION)
        , PUSH_UP          (0.25f, BUILT_IN_TRANSITION)
        , PUSH_DOWN        (0.25f, BUILT_IN_TRANSITION)
        ;

        public final float duration;
        public final String fragmentShaderFilename;
        TransitionType(float duration, String fragmentShaderFilename) {
            this.duration = duration;
            this.fragmentShaderFilename = fragmentShaderFilename;
        }
    }

    public static TransitionType getRandomTransition(){
        int index = MathUtils.random(TransitionType.values().length-1);
        return TransitionType.values()[index];
    }

    public static void initialize(ScreenManager<BaseScreen, ScreenTransition> screenManager) {
        // add transitions that use build-in transition classes
        Assets assets = Main.game.assets;
        screenManager.addScreenTransition(TransitionType.BLEND.name(), new BlendingTransition(assets.batch, 0.25f));
        screenManager.addScreenTransition(TransitionType.PUSH_UP.name(), new PushTransition(assets.batch, SlidingDirection.UP, 0.25f));
        screenManager.addScreenTransition(TransitionType.PUSH_DOWN.name(), new PushTransition(assets.batch, SlidingDirection.DOWN, 0.25f));

        // add 'custom' shader based transitions
        for (TransitionType type : TransitionType.values()){
            if (type.fragmentShaderFilename.equals(BUILT_IN_TRANSITION)) continue;

            Gdx.app.log("compiling transition", type.name());
            ZendoGLScreenTransition transition = new ZendoGLScreenTransition(type.duration);
            transition.compileGLTransition(Gdx.files.internal(type.fragmentShaderFilename).readString());
            screenManager.addScreenTransition(type.name(), transition);
        }
    }

}
