package com.github.tommyettinger.cg;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Like a {@link com.badlogic.gdx.graphics.g2d.Sprite}, but lighter-weight and customized to the conventions of this
 * demo.
 * <br>
 * Created by Tommy Ettinger on 12/20/2019.
 */
public class AnimatedSprite extends Sprite {
    public Animation<TextureRegion> animation;
    public long startTime;

    private AnimatedSprite()
    {
        super();
        setColor(0f, 0.5f, 0.5f, 1f);
    }

    public AnimatedSprite(Animation<TextureRegion> animation, float x, float y, int palette) {
        super(animation.getKeyFrame(0f));
        this.animation = animation;
        setPosition(x, y);
        setColor(palette % 160 / 255f, 0.5f, 0.5f, 1f);
        startTime = TimeUtils.millis();
    }

    public AnimatedSprite setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
        checkpoint();
        return this;
    }

    public AnimatedSprite checkpoint() {
        startTime = TimeUtils.millis();
        return this;
    }

    @Override
    public float[] getVertices() {
        final float time = TimeUtils.timeSinceMillis(startTime) * 1e-3f;
        setRegion(animation.getKeyFrame(time));
        if(animation.isAnimationFinished(time))
            checkpoint();
        return super.getVertices();
    }
}
