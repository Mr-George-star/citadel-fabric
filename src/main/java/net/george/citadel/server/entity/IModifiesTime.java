package net.george.citadel.server.entity;

import net.george.citadel.server.tick.modifier.TickRateModifier;

public interface IModifiesTime {
    boolean isTimeModificationValid(TickRateModifier modifier);
}
