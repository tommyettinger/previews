package com.github.tommyettinger.cg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.tommyettinger.ds.ObjectList;
import com.github.yellowstonegames.grid.IntPointHash;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    public TextureAtlas atlas;
    public Texture palettes;
    public SpriteBatch batch;
    public ShaderProgram shader;
    public Viewport viewport;
    public Camera camera;
    public long startTime;
    public ObjectList<Animation<Sprite>> terrain;
    public ObjectList<ObjectList<Animation<Sprite>>> units;
    public int seed = 1234;

    @Override
    public void create() {
        shader = new ShaderProgram(stuffSelectVertex, stuffSelectFragment);
        batch = new SpriteBatch(4000, shader);
        viewport = new ScreenViewport();
        camera = viewport.getCamera();
        atlas = new TextureAtlas("ColorGuard.atlas");
        palettes = new Texture("ColorGuardMasterPalette.png");
        terrain = new ObjectList<>(4);
        units = new ObjectList<>(ColorGuardData.units.size());
        for (int i = 0; i < 4; i++) {
            terrain.add(new Animation<>(0.0625f, atlas.createSprites("Terrain_angle" + i), Animation.PlayMode.LOOP));
        }
        for (int i = 0; i < ColorGuardData.units.size(); i++) {
            ColorGuardData.Unit unit = ColorGuardData.units.get(i);
            String name = unit.name;
            units.add(new ObjectList<>(4));
            for (int a = 0; a < 4; a++) {
                units.peek().add(new Animation<>(0.125f, atlas.createSprites(name + "_angle" + a), Animation.PlayMode.LOOP));
            }
            if(unit.primary != null) {
                for (int a = 0; a < 4; a++) {
                    units.peek().add(new Animation<>(0.125f, atlas.createSprites(name + "_Primary_angle" + a), Animation.PlayMode.LOOP));
                }
            }
            if(unit.secondary != null) {
                for (int a = 0; a < 4; a++) {
                    units.peek().add(new Animation<>(0.125f, atlas.createSprites(name + "_Secondary_angle" + a), Animation.PlayMode.LOOP));
                }
            }
        }

        startTime = TimeUtils.millis();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.5f, 0.5f, 0.5f, 1f);
        float moveAmt = Gdx.graphics.getDeltaTime() * 150f;

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            camera.position.y -= moveAmt;
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            camera.position.y += moveAmt;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            camera.position.x -= moveAmt;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            camera.position.x += moveAmt;
        camera.update();
        viewport.apply(false);
        batch.setProjectionMatrix(camera.combined);

        palettes.bind(1);
        batch.begin();

        shader.setUniformi("u_texPalette", 1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        final long time = TimeUtils.timeSinceMillis(startTime);
//        batch.setColor((208 + (time>>>12)%12)/255f, 0.5f, 0.5f, 1f);
//        batch.setColor((time >>> 12) % 208 / 255f, 0.5f, 0.5f, 1f);

        int angle;
        Sprite s;
        int ux = MathUtils.ceil((camera.position.x) / 60f);
        int uy = MathUtils.ceil((camera.position.y) / 30f);
        int upperX = (ux + uy) / 2;
        int upperY = (uy - ux) / 2;
        for (int x = upperX, nx = upperX - 18; x >= nx; x--) {
            for (int y = upperY, ny = upperY - 18; y >= ny; y--) {
                int hash = IntPointHash.hashAll(x, y, seed);
                s = terrain.get(hash & 3).getKeyFrame(time * 1e-3f);
                s.setPosition((x - y) * 60 - 60, (x + y) * 30 + 446);
                s.setColor((208 + ColorGuardData.terrains.indexOf(ColorGuardData.queryTerrain(x, y, seed))) / 255f, 0.5f, 0.5f, 1f);
                s.draw(batch);
                if((x & y & 1) == 1) {
                    angle = (int) ((time - hash & 0xFFFFFF) * 1e-3) & 15;
                    ObjectList<Animation<Sprite>> angles = units.get((hash>>>16)%units.size());
                    s = angles.get(angle % angles.size()).getKeyFrame((time - hash & 0xFFFFFF) * 1e-3f);
                    s.setPosition((x - y) * 60 - 60, (x + y) * 30 + 446);
                    s.setColor((hash >>> 6) % 208 / 255f, 0.5f, 0.5f, 1f);
                    s.draw(batch);
                }
            }
        }
//        for (int x = 19; x >= 0; x--) {
//            for (int y = 19; y >= 0; y--) {
//                int hash = IntPointHash.hashAll(x, y, seed);
//                s = terrain.get(hash & 3).getKeyFrame(time * 1e-3f);
//                s.setPosition((x - y) * 60 + 300, (x + y) * 30 - 154);
//                s.setColor((208 + ColorGuardData.terrains.indexOf(ColorGuardData.queryTerrain(x, y, seed))) / 255f, 0.5f, 0.5f, 1f);
//                s.draw(batch);
//                if((x & y & 1) == 1) {
//                    angle = (int) ((time - hash & 0xFFFFFF) * 1e-3) & 15;
//                    ObjectList<Animation<Sprite>> angles = units.get((hash>>>16)%units.size());
//                    s = angles.get(angle % angles.size()).getKeyFrame((time - hash & 0xFFFFFF) * 1e-3f);
//                    s.setPosition((x - y) * 60 + 300, (x + y) * 30 - 154);
//                    s.setColor((hash >>> 6) % 208 / 255f, 0.5f, 0.5f, 1f);
//                    s.draw(batch);
//                }
//            }
//        }
        batch.end();

    }

    public static final String stuffSelectVertex = "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "void main()\n" +
            "{\n" +
            "v_color = a_color;\n" +
            "v_color.a = v_color.a * (255.0/254.0);\n" +
            "v_texCoords = a_texCoord0;\n" +
            "gl_Position = u_projTrans * a_position;\n" +
            "}\n";
    public static final String stuffSelectFragment =
            "#ifdef GL_ES\n" +
                    "#define LOWP lowp\n" +
                    "precision mediump float;\n" +
                    "#else\n" +
                    "#define LOWP\n" +
                    "#endif\n" +
                    "varying LOWP vec4 v_color;\n" +
                    "varying vec2 v_texCoords;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform sampler2D u_texPalette;\n" +
                    "const vec3 forward = vec3(1.0 / 3.0);\n" +
                    "void main()\n" +
                    "{\n" +
                    "  vec4 color = texture2D(u_texture, v_texCoords);\n" +
                    "  vec4 index = vec4(color.rgb * (254.0 / 255.0), v_color.r);\n" +
                    "  vec3 tgt = texture2D(u_texPalette, index.xw).rgb;\n" +
                    "  vec3 lab = mat3(+0.2104542553, +1.9779984951, +0.0259040371, +0.7936177850, -2.4285922050, +0.7827717662, -0.0040720468, +0.4505937099, -0.8086757660) *" +
                    "             pow(mat3(0.4121656120, 0.2118591070, 0.0883097947, 0.5362752080, 0.6807189584, 0.2818474174, 0.0514575653, 0.1074065790, 0.6302613616) \n" +
                    "             * (tgt.rgb * tgt.rgb), forward);\n" +
                    "  lab.x = clamp(lab.x + index.y + v_color.g - 0.75, 0.0, 1.0);\n" +
                    "  lab.yz = clamp(lab.yz * (2.0 * color.b) * (0.5 + v_color.b), -1.0, 1.0);\n" +
                    "  lab = mat3(1.0, 1.0, 1.0, +0.3963377774, -0.1055613458, -0.0894841775, +0.2158037573, -0.0638541728, -1.2914855480) * lab;\n" +
                    "  gl_FragColor = vec4(sqrt(clamp(" +
                    "                 mat3(+4.0767245293, -1.2681437731, -0.0041119885, -3.3072168827, +2.6093323231, -0.7034763098, +0.2307590544, -0.3411344290, +1.7068625689) *\n" +
                    "                 (lab * lab * lab)," +
                    "                 0.0, 1.0)), v_color.a * color.a);\n" +
                    "}\n";
}