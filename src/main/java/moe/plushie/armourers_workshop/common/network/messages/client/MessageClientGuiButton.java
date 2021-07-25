package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiButton implements IMessage, IMessageHandler<MessageClientGuiButton, IMessage> {

    byte buttonId;

    public MessageClientGuiButton() {
    }

    public MessageClientGuiButton(byte buttonId) {
        this.buttonId = buttonId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buttonId = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(buttonId);
    }

    @Override
    public IMessage onMessage(MessageClientGuiButton message, MessageContext ctx) {
        ServerPlayerEntity player = ctx.getServerHandler().player;
        if (player == null) {
            return null;
        }
        Container container = player.openContainer;
        if (container instanceof IButtonPress) {
            ((IButtonPress) container).buttonPressed(ctx.getServerHandler().player, message.buttonId);
        }
        return null;
    }

    public static interface IButtonPress {
        public void buttonPressed(ServerPlayerEntity player, byte buttonId);
    }
}
