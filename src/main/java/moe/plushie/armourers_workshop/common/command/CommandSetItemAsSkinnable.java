package moe.plushie.armourers_workshop.common.command;

import java.util.Arrays;
import java.util.List;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;
import moe.plushie.armourers_workshop.common.command.wardrobe.CommandWardrobeSetOption;
import moe.plushie.armourers_workshop.common.config.ConfigHandlerOverrides;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.command.EnumArgument;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class CommandSetItemAsSkinnable extends ModCommand {

    public CommandSetItemAsSkinnable(ModCommand parent) {
        super(parent, "set_item_skinnable");
    }
    private enum Op{
        add(0),remove(1);
        public final int val;
        private Op(int val){
            this.val=val;
        }
    }
    /*
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 2) {
            String[] values = new String[ItemOverrideType.values().length];
            for (int i = 0; i < values.length; i++) {
                values[i] = ItemOverrideType.values()[i].toString().toLowerCase();
            }
            return getListOfStringsMatchingLastWord(args, values);
        }
        if (args.length == 3) {
            String[] values = new String[] { "add", "remove" };
            return getListOfStringsMatchingLastWord(args, values);
        }
        return null;
    }
*/
    @Override
    public LiteralArgumentBuilder buildCommand(){
        EnumArgument<ItemOverrideType> itemOverrideTypeEnumArgument = EnumArgument.enumArgument(ItemOverrideType.class);
        EnumArgument<Op> opEnumArgument = EnumArgument.enumArgument(Op.class);
        return literal(this.getName()).then(
                argument("player", EntityArgument.players()).then(
                        argument("type", itemOverrideTypeEnumArgument).then(
                                argument("op", opEnumArgument).executes((x)->this.execute(x))
                        )
                ));
    }
    @Override
    public int execute(CommandContext<CommandSource> ctx) throws CommandException, CommandSyntaxException {
        ServerPlayerEntity player =EntityArgument.getPlayer(ctx, "player");
        ItemOverrideType type=ctx.getArgument("type", ItemOverrideType.class);
        Op op = ctx.getArgument("op", Op.class);
        ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty()) {
            if (op.equals(Op.add)) {
                ConfigHandlerOverrides.addOverride(type, stack.getItem());
            } else if (op.equals(Op.remove)) {
                ConfigHandlerOverrides.removeOverride(type, stack.getItem());
            }
        }
        return 0;
    }
}
