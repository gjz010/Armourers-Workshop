package moe.plushie.armourers_workshop.common.command.wardrobe;

import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.command.ModCommand;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class CommandWardrobeSetUnlockedSlots extends ModCommand {

    public CommandWardrobeSetUnlockedSlots(ModCommand parent) {
        super(parent, "set_unlocked_slots");
    }

    @Override
    public LiteralArgumentBuilder buildCommand(){
        return literal(this.getName()).then(
                argument("player", EntityArgument.players()).then(
                        argument("skin_name", StringArgumentType.string()).then(
                            argument("count", IntegerArgumentType.integer(0, EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE))
                                    .executes((x)->this.execute(x))
                        )
                ));
    }

    // Arguments 3 - <player> <skin type> <count>
    @Override
    public int execute(CommandContext ctx) throws CommandException, CommandSyntaxException {
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        String skinTypeName = StringArgumentType.getString(ctx, "skin_name");
        int count = IntegerArgumentType.getInteger(ctx, "count");

        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(skinTypeName);
        if (skinType == null) {
            throw new CommandException(new StringTextComponent("bad skin type: "+skinTypeName));
        }

        IPlayerWardrobeCap wardrobeCap = (IPlayerWardrobeCap) PlayerWardrobeCap.get(player);
        if (wardrobeCap != null) {
            ModLogger.log("setting count " + count + " on " + skinType.getRegistryName());
            wardrobeCap.setUnlockedSlotsForSkinType(skinType, count);
            wardrobeCap.syncToPlayer(player);
            wardrobeCap.syncToAllTracking();
        }
        return 0;
    }

}
