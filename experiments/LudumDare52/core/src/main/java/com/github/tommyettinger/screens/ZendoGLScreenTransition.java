package com.github.tommyettinger.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.damios.guacamole.Preconditions;
import de.damios.guacamole.gdx.graphics.QuadMeshGenerator;
import de.damios.guacamole.gdx.graphics.ShaderProgramFactory;
import de.eskalon.commons.screen.transition.TimedTransition;

import javax.annotation.Nullable;

public class ZendoGLScreenTransition extends TimedTransition {

    private static final String VERT_SHADER =
//            "#version 330\n" +
//            "\n" +
            "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "\n" +
            "attribute vec3 a_position;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "\n" +
            "uniform mat4 u_projTrans;\n" +
            "\n" +
            "varying vec3 v_position;\n" +
            "varying vec2 v_texCoord0;\n" +
            "\n" +
            "void main() {\n" +
            "    v_position = a_position;\n" +
            "    v_texCoord0 = a_texCoord0;\n" +
            "    gl_Position = u_projTrans * vec4(a_position, 1.0);\n" +
            "}\n";

    private static final String FRAG_SHADER_PREPEND =
//            "#version 330\n" +
//            "\n" +
            "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "\n" +
            "uniform sampler2D lastScreen;\n" +
            "uniform sampler2D currScreen;\n" +
            "uniform float ratio;\n" +
            "uniform float progress;\n" +
            "\n" +
            "varying vec3 v_position;\n" +
            "varying vec2 v_texCoord0;\n" +
            "\n" +
//            "vec4 fragColor;\n" +
//            "\n" +
            "vec4 getToColor(vec2 uv) {\n" +
            "    return texture2D(currScreen, uv);\n" +
            "}\n" +
            "\n" +
            "vec4 getFromColor(vec2 uv) {\n" +
            "    return texture2D(lastScreen, uv);\n" +
            "}\n";

    private static final String FRAG_SHADER_POSTPEND =
            "void main() {\n" +
            "    gl_FragColor = transition(v_texCoord0);\n" +
            "}\n";

    protected ShaderProgram program;
    protected Viewport viewport;

    private RenderContext renderContext;

    /**
     * A screen filling quad.
     */
    private Mesh screenQuad;
    private int projTransLoc;
    private int lastScreenLoc, currScreenLoc;
    private int progressLoc;
    private int ratioLoc;

    public ZendoGLScreenTransition(float duration) {
        this(duration, null);
    }

    /**
     * Creates a shader transition.
     * <p>
     * The shader {@linkplain #compileShader(String, String, boolean) has to be
     * compiled} before {@link #create()} is called.
     *
     * @param duration
     *            the duration of the transition
     * @param interpolation
     *            the interpolation to use
     */
    public ZendoGLScreenTransition(float duration, @Nullable Interpolation interpolation) {
        super(duration, interpolation);
    }

    /**
     * The GL Transitions shader code has to be set via this method.
     * <p>
     * Do not forget to uncomment/set the uniforms that act as transition
     * parameters! Please note that in GLSL EL (Web, Android, iOS) uniforms
     * cannot be set from within the shader code, so this has to be done in Java
     * via {@link #getProgram()} instead!
     * <p>
     * Furthermore, do not forget to replace {@code ratio} in the code with your
     * screen ratio (width / height).
     * <p>
     * Ignores code in {@link ShaderProgram#prependFragmentCode} and
     * {@link ShaderProgram#prependVertexCode}.
     *
     * @param glTransitionsCode
     *            the GL Transitions shader code;
     */
    public void compileGLTransition(String glTransitionsCode) {
        compileShader(VERT_SHADER, FRAG_SHADER_PREPEND + glTransitionsCode + FRAG_SHADER_POSTPEND, true);
    }

    /**
     * @param vert
     *            the vertex shader code
     * @param frag
     *            the fragment shader code
     * @param ignorePrepend
     *            whether to ignore the code in
     *            {@link ShaderProgram#prependFragmentCode} and
     *            {@link ShaderProgram#prependVertexCode}
     */
    public void compileShader(String vert, String frag, boolean ignorePrepend) {
        Preconditions.checkNotNull(vert, "The vertex shader cannot be null.");
        Preconditions.checkNotNull(frag, "The fragment shader cannot be null.");

        program = ShaderProgramFactory.fromString(vert, frag, true, ignorePrepend);
    }

    @Override
    protected void create() {
        Preconditions.checkState(program != null, "The shader has to be compiled before the transition can be created!");

        // renders the transition over the whole screen
        viewport = new ScreenViewport();

        projTransLoc = program.getUniformLocation("u_projTrans");
        lastScreenLoc = program.getUniformLocation("lastScreen");
        currScreenLoc = program.getUniformLocation("currScreen");
        ratioLoc = program.getUniformLocation("ratio");
        progressLoc = program.getUniformLocation("progress");

        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN));
    }

    @Override
    public void render(float delta, TextureRegion lastScreen, TextureRegion currScreen, float progress) {
        viewport.apply();

        renderContext.begin();
        program.bind();

        // Set uniforms
        float aspectRatio = Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        program.setUniformMatrix(projTransLoc, viewport.getCamera().combined);
        program.setUniformf(progressLoc, progress);
        program.setUniformf(ratioLoc, aspectRatio);
        program.setUniformi(lastScreenLoc, renderContext.textureBinder.bind(lastScreen.getTexture()));
        program.setUniformi(currScreenLoc, renderContext.textureBinder.bind(currScreen.getTexture()));

        // Render the screens using the shader
        screenQuad.render(program, GL20.GL_TRIANGLE_STRIP);

        renderContext.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);

        if (this.screenQuad != null) {
            this.screenQuad.dispose();
        }
        this.screenQuad = QuadMeshGenerator.createFullScreenQuad(width, height, true);
    }

    @Override
    public void dispose() {
        if (program != null) {
            program.dispose();
        }
        if (screenQuad != null) {
            screenQuad.dispose();
        }
    }

    /**
     * @return the used shader
     */
    public ShaderProgram getProgram() {
        return program;
    }

}
