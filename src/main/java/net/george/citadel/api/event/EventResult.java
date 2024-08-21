package net.george.citadel.api.event;

import net.minecraft.util.ActionResult;

/**
 * Duplicate the Event.Result class from the Forge Event API to match the result of an event in Forge.
 * @author Mr.George
 */
@SuppressWarnings("unused")
public enum EventResult {
    DENY,
    DEFAULT,
    ALLOW;

    /**
     * Converts this to an {@link ActionResult action result} in vanilla.
     * @return The {@link ActionResult result} after conversion.
     */
    public ActionResult toActionResult() {
        return switch (this) {
            case DENY -> ActionResult.FAIL;
            case ALLOW -> ActionResult.SUCCESS;
            case DEFAULT -> ActionResult.PASS;
        };
    }
}
