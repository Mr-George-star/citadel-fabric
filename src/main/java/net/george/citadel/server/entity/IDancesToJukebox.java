package net.george.citadel.server.entity;

import net.george.citadel.Citadel;
import net.george.citadel.server.message.DanceJukeboxMessage;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public interface IDancesToJukebox {
    void setDancing(boolean dancing);

    void setJukeboxPos(BlockPos pos);

    default void onClientPlayMusicDisc(int entityId, BlockPos pos, boolean dancing) {
        Citadel.sendMSGToServer(new DanceJukeboxMessage(entityId, dancing, pos));
        this.setDancing(dancing);
        if (dancing) {
            this.setJukeboxPos(pos);
        } else {
            this.setJukeboxPos(null);
        }
    }
}
