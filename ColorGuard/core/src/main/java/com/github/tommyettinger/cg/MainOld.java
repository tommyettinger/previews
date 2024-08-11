package com.github.tommyettinger.cg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.tommyettinger.ds.IntList;
import com.github.tommyettinger.ds.ObjectList;
import com.github.yellowstonegames.grid.IntPointHash;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainOld extends ApplicationAdapter {

    public TextureAtlas atlas;
    public Texture palettes;
    public SpriteBatch batch;
    public ShaderProgram shader;
    public ScreenViewport viewport;
    public Camera camera;
    public long startTime;
    public ObjectList<Animation<Sprite>> terrain;
    public ObjectList<ObjectList<Animation<Sprite>>> units;
    public ObjectList<ObjectList<Animation<Sprite>>> receives;
    public BitmapFont font;
    public int seed = 1234;
//    private GLProfiler profiler;

    @Override
    public void create() {
//        Gdx.app.setLogLevel(Application.LOG_INFO);
//        profiler = new GLProfiler(Gdx.graphics);
        shader = new ShaderProgram(stuffSelectVertex, stuffSelectFragmentAltered);
        batch = new SpriteBatch(1000, shader);
        viewport = new ScreenViewport();
        camera = viewport.getCamera();
        atlas = new TextureAtlas("ColorGuard.atlas");
        font = new BitmapFont(Gdx.files.internal("NanoOKExtended.fnt"), atlas.findRegion("NanoOKExtended"));
        palettes = new Texture("ColorGuardMasterPalette.png");
        terrain = new ObjectList<>(4);
        units = new ObjectList<>(ColorGuardData.units.size());
        receives = new ObjectList<>(ColorGuardData.units.size());
        for(ColorGuardData.Terrain t : ColorGuardData.Terrain.ALL) {
            for (int i = 0; i < 4; i++) {
//            terrain.add(new Animation<>(0.0625f, atlas.createSprites("Terrain_angle" + i), Animation.PlayMode.LOOP));
                terrain.add(new Animation<>(0.0625f, atlas.createSprites(t+"_angle" + i), Animation.PlayMode.LOOP));
            }
        }
        for (int i = 0; i < ColorGuardData.units.size(); i++) {
            ColorGuardData.Unit unit = ColorGuardData.units.get(i);
            String name = unit.name;
            units.add(new ObjectList<>(16));
            receives.add(new ObjectList<>(8));
            for (int a = 0; a < 4; a++) {
                units.peek().add(new Animation<>(0.125f, atlas.createSprites(name + "_angle" + a), Animation.PlayMode.LOOP));
            }
            if(unit.primary != null) {
                for (int a = 0; a < 4; a++) {
                    units.peek().add(new Animation<>(0.125f, atlas.createSprites(name + "_Primary_angle" + a), Animation.PlayMode.LOOP));
                    receives.peek().add(new Animation<>(0.125f, atlas.createSprites(unit.primary + "_Receive_" + unit.primaryStrength + "_angle" + (a+2&3))));
                }
            }
            if(unit.secondary != null) {
                for (int a = 0; a < 4; a++) {
                    units.peek().add(new Animation<>(0.125f, atlas.createSprites(name + "_Secondary_angle" + a), Animation.PlayMode.LOOP));
                    receives.peek().add(new Animation<>(0.125f, atlas.createSprites(unit.secondary + "_Receive_" + unit.secondaryStrength + "_angle" + (a+2&3))));
                }
            }
        }

        Gdx.input.setInputProcessor(new GestureDetector(new GestureDetector.GestureAdapter(){
            @Override
            public boolean zoom(float initialDistance, float distance) {
                if((TimeUtils.timeSinceMillis(startTime) & 63) < 3) {
                    if (initialDistance < distance)
                        viewport.setUnitsPerPixel(viewport.getUnitsPerPixel() * 0.5f);
                    else
                        viewport.setUnitsPerPixel(viewport.getUnitsPerPixel() * 2f);
                    viewport.update(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), false);
                }
                return super.zoom(initialDistance, distance);
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                camera.position.add(-deltaX * viewport.getUnitsPerPixel(), deltaY * viewport.getUnitsPerPixel(), 0f);
                return super.pan(x, y, deltaX, deltaY);
            }

            @Override
            public boolean longPress(float x, float y) {
                seed += Math.signum(y - Gdx.graphics.getHeight() * 0.5f);
                return super.longPress(x, y);
            }
        }));
//        profiler.enable();
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
        if(Gdx.input.isKeyJustPressed(Input.Keys.I) || Gdx.input.isKeyJustPressed(Input.Keys.EQUALS) || Gdx.input.isKeyJustPressed(Input.Keys.PLUS))
        {
            viewport.setUnitsPerPixel(viewport.getUnitsPerPixel() * 0.5f);
            viewport.update(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), false);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.O) || Gdx.input.isKeyJustPressed(Input.Keys.MINUS))
        {
            viewport.setUnitsPerPixel(viewport.getUnitsPerPixel() * 2f);
            viewport.update(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), false);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.R))
            ++seed;
        if(Gdx.input.isKeyJustPressed(Input.Keys.E))
            --seed;
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();
        camera.position.set(MathUtils.round(camera.position.x), MathUtils.round(camera.position.y), camera.position.z);
        viewport.apply(false);
        batch.setProjectionMatrix(camera.combined);

        palettes.bind(1);
        batch.begin();

        shader.setUniformi("u_texPalette", 1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        long time = TimeUtils.timeSinceMillis(startTime);
        if(time > 16000) {
            startTime = TimeUtils.millis();
            time = 0;
        }
//        batch.setColor((208 + (time>>>12)%12)/255f, 0.5f, 0.5f, 1f);
//        batch.setColor((time >>> 12) % 208 / 255f, 0.5f, 0.5f, 1f);

        int angle;
        Sprite s, rec;
        int ux = MathUtils.ceil((camera.position.x) / 40f);
        int uy = MathUtils.ceil((camera.position.y) / 20f);
//        int ux = MathUtils.ceil((camera.position.x) / 60f);
//        int uy = MathUtils.ceil((camera.position.y) / 30f);
        int upperX = (ux + uy) / 2;
        int upperY = (uy - ux) / 2;
        for (int x = upperX, nx = upperX - 27; x >= nx; x--) {
            for (int y = upperY, ny = upperY - 27; y >= ny; y--) {
                int hash = IntPointHash.hashAll(x, y, seed);
                ColorGuardData.Terrain q = ColorGuardData.queryTerrain(x, y, seed);
                s = terrain.get(q.ordinal() << 2).getKeyFrame(0);
                s.setPosition((x - y) * 40 - 40, (x + y) * 20 + 450 - 4);
                s.setColor(0f, 0.65f + (hash * 0x3p-37f), 0.4f, 1f);
//                s.setColor((160 + q.ordinal()) / 255f, 0.5f, 0.5f, 1f);
                s.draw(batch);
            }
        }
        for (int x = upperX, nx = upperX - 27; x >= nx; x--) {
            for (int y = upperY, ny = upperY - 27; y >= ny; y--) {
                int hash = IntPointHash.hashAll(x, y, seed);
                ColorGuardData.Terrain q = ColorGuardData.queryTerrain(x, y, seed);
                if(hash >>> 27 < 3) {
//                if(hash >>> 27 < 5) {
//                if((x & y & 1) == 1) {
                    IntList ps = ColorGuardData.placeable.get(q);
                    int psi = ps.get((hash >>> 16) % ps.size());
                    ObjectList<Animation<Sprite>> angles = units.get(psi);
                    ColorGuardData.Unit unit = ColorGuardData.units.get(psi);
                    angle = (int) (time * 0.5e-3f) % angles.size();
                    float t = time * 1e-3f % 2f;
                    s = angles.get(angle).getKeyFrame(t);
                    if(angle >= 4) {
                        Animation<Sprite> recAnim = receives.get(psi).get(angle - 4);
                        int rx = x, ry = y, range = (angle < 8) ? unit.primaryRange : unit.secondaryRange;
                        if(t >= 0.25f + range * 0.25f && !recAnim.isAnimationFinished(t - 0.25f - range * 0.25f)) {
                            rec = recAnim.getKeyFrame(t - 0.25f - range * 0.25f, false);
                            if ((angle & 1) == 0) rx -= range * (-(angle & 2) >> 31 | 1);
                            else ry -= range * (-(angle & 2) >> 31 | 1);
                            rec.setPosition((rx - ry) * 40 - 40, (rx + ry) * 20 + 450);
                            rec.setColor((hash >>> 6) % 160 / 255f, 0.5f, 0.5f, 1f);
                        }
                        else rec = null;
                    }
                    else rec = null;
                    s.setPosition((x - y) * 40 - 40, (x + y) * 20 + 450);
                    s.setColor((hash >>> 6) % 160 / 255f, 0.5f, 0.5f, 1f);
                    s.draw(batch);
                    if(rec != null)
                        rec.draw(batch);
                }
            }
        }
//        for (int x = 19; x >= 0; x--) {
//            for (int y = 19; y >= 0; y--) {
//                int hash = IntPointHash.hashAll(x, y, seed);
//                s = terrain.get(hash & 3).getKeyFrame(time * 1e-3f);
//                s.setPosition((x - y) * 60 + 300, (x + y) * 30 - 154);
//                s.setColor((208 + ColorGuardData.queryTerrain(x, y, seed).ordinal()) / 255f, 0.5f, 0.5f, 1f);
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
        font.setColor(0f, 0f, 0.5f, 1f);
        font.draw(batch, Gdx.graphics.getFramesPerSecond() + " fps", camera.position.x - viewport.getWorldWidth() * 0.4f, camera.position.y + viewport.getWorldHeight() * 0.4f);
        batch.end();
//        if((TimeUtils.timeSinceMillis(startTime) & 0x3F0) == 0x3F0)
//            Gdx.app.log("(PERFORMANCE)", "Calls: " + profiler.getCalls() + ", Draw Calls: " + profiler.getDrawCalls() +
//                ", Shader Switches: " + profiler.getShaderSwitches() + ", Vertex Count: " + profiler.getVertexCount());
//        profiler.reset();
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
                    "precision highp float;\n" +
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
                    "  vec4 index = vec4(color.rgb, v_color.r);\n" +
                    "  index.rgb *= (254.0 / 255.5);\n" +
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
    public static final String stuffSelectFragmentAltered =
//            "#ifdef GL_ES\n" +
//                    "#define LOWP lowp\n" +
//                    "precision highp float;\n" +
//                    "#else\n" +
//                    "#define LOWP\n" +
//                    "#endif\n" +
//                    "varying LOWP vec4 v_color;\n" +
//                    "varying vec2 v_texCoords;\n" +
//                    "uniform sampler2D u_texture;\n" +
//                    "uniform sampler2D u_texPalette;\n" +
//                    "const vec3 forward = vec3(1.0 / 3.0);\n" +
//                    "void main()\n" +
//                    "{\n" +
//                    "  vec4 color = texture2D(u_texture, v_texCoords);\n" +
//                    "  vec4 index = vec4(color.rgb, v_color.r);\n" +
//                    "  index.rgb *= (254.0 / 255.5);\n" +
//                    "  vec3 tgt = texture2D(u_texPalette, index.xw).rgb;\n" +
//                    "  vec3 lab = mat3(+0.2104542553, +1.9779984951, +0.0259040371, +0.7936177850, -2.4285922050, +0.7827717662, -0.0040720468, +0.4505937099, -0.8086757660) *" +
//                    "             pow(mat3(0.4121656120, 0.2118591070, 0.0883097947, 0.5362752080, 0.6807189584, 0.2818474174, 0.0514575653, 0.1074065790, 0.6302613616) \n" +
//                    "             * (tgt.rgb * tgt.rgb), forward);\n" +
//
//                    "  lab.x = smoothstep(0.0, 1.0, lab.x + index.y + v_color.g - 0.75);\n" +
//                    "  lab.y = clamp(lab.y * (1.5 * color.b) * (0.5 + v_color.b), -1.0, 1.0);\n" +
//                    "  lab.z = clamp(lab.z * (1.5 * color.b) * (0.5 + v_color.b) + (sqrt(lab.x) - 0.8) * 0.25, -1.0, 1.0);\n" +
//                    "  lab = mat3(1.0, 1.0, 1.0, +0.3963377774, -0.1055613458, -0.0894841775, +0.2158037573, -0.0638541728, -1.2914855480) * lab;\n" +
//                    "  gl_FragColor = vec4(sqrt(clamp(" +
//                    "                 mat3(+4.0767245293, -1.2681437731, -0.0041119885, -3.3072168827, +2.6093323231, -0.7034763098, +0.2307590544, -0.3411344290, +1.7068625689) *\n" +
//                    "                 (lab * lab * lab)," +
//                    "                 0.0, 1.0)), v_color.a * color.a);\n" +
//                    "}\n";
            "#ifdef GL_ES\n" +
                    "#define LOWP lowp\n" +
                    "precision highp float;\n" +
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
                    "  vec4 index = vec4(color.rgb, v_color.r);\n" +
                    "  index.rgb *= (254.0 / 255.5);\n" +
                    "  vec3 tgt = texture2D(u_texPalette, index.xw).rgb;\n" +
                    "  vec3 lab = mat3(+0.2104542553, +1.9779984951, +0.0259040371, +0.7936177850, -2.4285922050, +0.7827717662, -0.0040720468, +0.4505937099, -0.8086757660) *" +
                    "             pow(mat3(0.4121656120, 0.2118591070, 0.0883097947, 0.5362752080, 0.6807189584, 0.2818474174, 0.0514575653, 0.1074065790, 0.6302613616) \n" +
                    "             * (tgt.rgb * tgt.rgb), forward);\n" +

                    "  lab.x = clamp((lab.x + index.y + v_color.g) - 0.87, 0.0, 1.0);\n" +
                    "  lab.x = pow(lab.x, 0.666666);\n" +
//                    "  lab.x = smoothstep(0.0, 1.0, (lab.x + index.y + v_color.g) - 1.0);\n" +
                    "  lab.y = clamp(lab.y * (3.8 * color.b) * (v_color.b), -1.0, 1.0);\n" +
                    "  lab.z = clamp(lab.z * (3.8 * color.b) * (v_color.b) + asin((lab.x - 1.0) * 0.7) * 0.125 * (1.0 - lab.y * lab.y), -1.0, 1.0);\n" +
                    "  lab = mat3(1.0, 1.0, 1.0, +0.3963377774, -0.1055613458, -0.0894841775, +0.2158037573, -0.0638541728, -1.2914855480) * lab;\n" +
                    "  gl_FragColor = vec4(sqrt(clamp(" +
                    "                 mat3(+4.0767245293, -1.2681437731, -0.0041119885, -3.3072168827, +2.6093323231, -0.7034763098, +0.2307590544, -0.3411344290, +1.7068625689) *\n" +
                    "                 (lab * lab * lab)," +
                    "                 0.0, 1.0)), v_color.a * color.a);\n" +
                    "}\n";

}
