package net.george.citadel.server.entity;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

@SuppressWarnings("unused")
public interface IComandableMob {
    int getCommand();

    void setCommand(int command);

    default ActionResult playerSetCommand(PlayerEntity owner, AnimalEntity ourselves) {
        if (!owner.world.isClient) {
            int command = (getCommand() + 1) % 3;
            this.setCommand(command);
            this.sendCommandMessage(owner, command, ourselves.getName());

            if (ourselves instanceof TameableEntity){
                ((TameableEntity)(ourselves)).setSitting(command == 1);
            }
        }
        return ActionResult.PASS;
    }

    default void sendCommandMessage(PlayerEntity owner, int command, Text name){
    }
}
