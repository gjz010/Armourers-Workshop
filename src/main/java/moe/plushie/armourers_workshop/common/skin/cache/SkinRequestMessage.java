package moe.plushie.armourers_workshop.common.skin.cache;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import net.minecraft.entity.player.ServerPlayerEntity;

public class SkinRequestMessage {
    
    private final ISkinIdentifier skinIdentifier;
    private final ServerPlayerEntity player;
    
    public SkinRequestMessage(ISkinIdentifier skinIdentifier, ServerPlayerEntity player) {
        this.skinIdentifier = skinIdentifier;
        this.player = player;
    }
    
    public ISkinIdentifier getSkinIdentifier() {
        return skinIdentifier;
    }
    
    public ServerPlayerEntity getPlayer() {
        return player;
    }
}
