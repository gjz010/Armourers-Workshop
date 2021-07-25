package moe.plushie.armourers_workshop.common.command.wardrobe;

import java.util.List;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.command.ModCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.command.EnumArgument;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.arguments.EntityArgument.getPlayer;

public class CommandWardrobeSetOption extends ModCommand {
    private enum SubOptions{
        showFootArmour(0),
        showLegArmour(1),
        showChestArmour(2),
        showHeadArmour(3);

        private final int value;
        private SubOptions(int value) {
            this.value = value;
        }

        public int val(){
            return this.value;
        }
    }
    private static final String[] SUB_OPTIONS = new String[] { "showFootArmour", "showLegArmour", "showChestArmour", "showHeadArmour" };

    public CommandWardrobeSetOption(ModCommand parent) {
        super(parent, "set_option");
    }

    @Override
    public LiteralArgumentBuilder buildCommand(){
        EnumArgument<SubOptions> subOptionsEnumArgument = EnumArgument.enumArgument(SubOptions.class);
        return literal(this.getName()).then(
                argument("player", EntityArgument.players()).then(
                        argument("option", subOptionsEnumArgument).then(
                                argument("value", BoolArgumentType.bool()).executes((x)->this.execute(x))
                        )
                ));
    }


    // Arguments 3 - <player> <option> <value>
    @Override
    public int execute(CommandContext ctx) throws CommandException, CommandSyntaxException {
        ServerPlayerEntity player = getPlayer(ctx, "player");
        int subOptionIndex = ((SubOptions)ctx.getArgument("option", SubOptions.class)).val();
        boolean argValue = BoolArgumentType.getBool(ctx, "value");
        IPlayerWardrobeCap wardrobeCap = (IPlayerWardrobeCap) PlayerWardrobeCap.get(player);
        if (wardrobeCap != null) {
            if (subOptionIndex < 4) {
                EquipmentSlotType slot = EquipmentSlotType.values()[subOptionIndex + 2];
                wardrobeCap.setArmourOverride(slot, !argValue);
                wardrobeCap.syncToPlayer(player);
                wardrobeCap.syncToAllTracking();
            }
        }
        return 0;
    }
}
