package com.github.tommyettinger;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Like a {@link Sprite}, but lighter-weight and customized to the conventions of this
 * demo.
 * <br>
 * Created by Tommy Ettinger on 12/20/2019.
 */
public class AnimatedSprite extends Sprite {
    public Animation<Sprite> animation;
    public long startTime;
    public ColorGuardData.Unit unit;

    private AnimatedSprite()
    {
        super();
        setColor(0f, 0.5f, 0.5f, 1f);
        unit = ColorGuardData.units.get(0);
    }

    public AnimatedSprite(Animation<Sprite> animation, float x, float y, int palette, ColorGuardData.Unit unit) {
        super(animation.getKeyFrame(0f));
        this.animation = animation;
        setPosition(x, y);
        setColor(palette % 160 / 255f, 0.5f, 0.5f, 1f);
        this.unit = unit;
        startTime = TimeUtils.millis();
    }

    public AnimatedSprite setAnimation(Animation<Sprite> animation) {
        this.animation = animation;
        checkpoint();
        return this;
    }

    public AnimatedSprite checkpoint() {
        startTime = TimeUtils.millis();
        return this;
    }

    @Override
    public void draw(Batch batch) {
        final float time = TimeUtils.timeSinceMillis(startTime) * 1e-3f;

        Sprite s = animation.getKeyFrame(time);
        s.setPosition(getX(), getY());
        s.setColor(getColor());
        s.draw(batch);
        if(animation.isAnimationFinished(time))
            checkpoint();
    }
}
