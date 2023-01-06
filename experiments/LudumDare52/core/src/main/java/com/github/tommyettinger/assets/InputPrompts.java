package com.github.tommyettinger.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.tommyettinger.Assets;

public class InputPrompts {

    private final TextureRegion sheet;
    private final TextureRegion[][] regions;

    public InputPrompts(Assets assets) {
        String name = "icons/input-prompts";
        this.sheet = assets.atlas.findRegion(name);
        if (this.sheet == null) {
            throw new GdxRuntimeException("Unable to find '" + name + "' region in texture atlas. Does sprites/" + name + " exist?");
        }

        this.regions = sheet.split(16, 16);
    }

    public TextureRegion get(Type type) {
        if (type.x < 0 || type.x >= regions[0].length
         || type.y < 0 || type.y >= regions.length) {
            throw new GdxRuntimeException("Can't get input prompt for type '" + type + "', invalid tilesheet coordinates: (" + type.x + ", " + type.y + ")");
        }
        return regions[type.y][type.x];
    }

    public enum Type {
        // TODO: add more entries as needed or when bored
        // letters
          key_light_a(18, 3)
        , key_light_b(23, 4)
        // numbers
        // ...Thanks Brian! Lots of numbers I see here :p
        , key_light_number_1(17, 1)
        , key_light_number_2(18, 1)
        , key_light_number_3(19, 1)
        , key_light_number_4(20, 1)
        , key_light_number_5(21, 1)
        , key_light_number_6(22, 1)
        , key_light_number_7(23, 1)
        , key_light_number_8(24, 1)
        , key_light_number_9(25, 1)
        , key_light_number_0(26, 1)

        // spacebar
        , key_light_spacebar_1(31, 6)
        , key_light_spacebar_2(32, 6)
        , key_light_spacebar_3(33, 6)
        // buttons
        , button_color_blank_green(0, 0)
        , button_color_blank_red(1, 0)
        , button_color_blank_blue(2, 0)
        , button_color_blank_yellow(3, 0)
        , button_light_blank(12, 0)
        , button_light_a(13, 0)
        , button_light_b(14, 0)
        , button_light_x(15, 0)
        , button_light_y(16, 0)
        , button_dark_blank(12, 1)
        , button_dark_a(13, 1)
        , button_dark_b(14, 1)
        , button_dark_x(15, 1)
        , button_dark_y(16, 1)
        , button_light_power(15, 20)
        , button_dark_power(15, 21)
        , button_light_tv(11, 20)
        , button_dark_tv(11, 21)
        // arrows
        , key_light_arrow_up(30, 4)
        , key_light_arrow_right(31, 4)
        , key_light_arrow_down(32, 4)
        , key_light_arrow_left(33, 4)
        , key_light_key_w(18, 2)
        , key_light_key_a(18, 3)
        , key_light_key_s(19, 3)
        , key_light_key_d(20, 3)
        , key_light_key_space(18, 2)
        , mouse_light_left(9, 2)
        , key_dark_arrow_up(30, 12)
        , key_dark_arrow_right(31, 12)
        , key_dark_arrow_down(32, 12)
        , key_dark_arrow_left(33, 12)
        // icons
        , hand_point_right(6, 17)
        , hand_point_left(5, 17)
        , hand_point_up(0, 17)
        , hand_point_down(1, 17)
        , hand_grab_start(3, 17)
        , hand_grab_middle(2, 17)
        , hand_grab_end(4, 17)
        , light_big_minus(13, 20)
        , light_big_plus(14, 20)
        , light_left_circle(10, 22)
        , light_right_circle(12, 22)
        ;

        public final int x;
        public final int y;
        Type(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}
