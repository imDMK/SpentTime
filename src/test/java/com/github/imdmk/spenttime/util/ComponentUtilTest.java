package com.github.imdmk.spenttime.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComponentUtilTest {

    @Test
    void testCreateItalic() {
        Component result = ComponentUtil.createItalic("SpentTime");
        Component excepted = Component.text("SpentTime").decoration(TextDecoration.ITALIC, false);

        assertEquals(excepted, result);
    }

    @Test
    void testDeserialize() {
        String resultContent = "<red>DMK";

        Component result = ComponentUtil.deserialize(resultContent);
        Component excepted = Component.text("DMK").color(NamedTextColor.RED);

        assertEquals(excepted, result);
    }

    @Test
    void testLegacyDeserialize() {
        String resultContent = "§4DMK";

        Component result = ComponentUtil.deserialize(resultContent);
        Component excepted = Component.text("DMK").color(NamedTextColor.DARK_RED);

        assertEquals(excepted, result);
    }
}
