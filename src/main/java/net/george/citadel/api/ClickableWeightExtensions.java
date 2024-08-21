package net.george.citadel.api;

import net.minecraft.client.gui.widget.ClickableWidget;

@SuppressWarnings("unused")
public interface ClickableWeightExtensions {
    default ClickableWidget self() {
        return (ClickableWidget) this;
    }

    default int getPackedFGColor() {
        return -1;
    }

    default void setPackedFGColor(int packedFGColor) {}

    default int getFGColor() {
        if (this.getPackedFGColor() != -1) {
            return this.getPackedFGColor();
        } else {
            return self().active ? 16777215 : 10526880;
        }
    }
}
