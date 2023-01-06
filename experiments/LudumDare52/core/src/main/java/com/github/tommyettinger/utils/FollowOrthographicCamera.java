package com.github.tommyettinger.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FollowOrthographicCamera extends OrthographicCamera {

    private static float ZOOM_SPEED = 1f;

    Vector2 direction;
    Vector2 tempVec;
    public float targetZoom;


    public FollowOrthographicCamera() {
        direction = new Vector2();
        this.tempVec = new Vector2();
        this.targetZoom = 1f;
    }

    public void update(Vector2 followPoint, Rectangle bounds, float dt) {
        tempVec.set(MathUtils.clamp(followPoint.x, bounds.x + (viewportWidth*zoom)/2, bounds.x + bounds.width - (viewportWidth*zoom)/2f),
                MathUtils.clamp(followPoint.y, bounds.y + (viewportHeight*zoom)/2, bounds.y + bounds.height - (viewportHeight*zoom)/2f));

        direction.set(tempVec).sub(position.x, position.y).scl(8.5f);

        position.add(direction.x * dt, direction.y * dt, 0);
        float dzoom = (targetZoom - zoom);
        float zoomChange = ZOOM_SPEED * dt;
        if (Math.abs(dzoom) < zoomChange){
            zoom = targetZoom;
        } else {
            if (dzoom < 0) {
                zoom -= zoomChange;
            } else {
                zoom += zoomChange;
            }
        }

        this.update();
    }
}
