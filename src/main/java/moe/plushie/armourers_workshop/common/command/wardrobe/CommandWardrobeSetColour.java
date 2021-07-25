package moe.plushie.armourers_workshop.common.command.wardrobe;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.api.common.IExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.command.ModCommand;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.server.command.EnumArgument;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.arguments.EntityArgument.getPlayer;

public class CommandWardrobeSetColour extends ModCommand {

    public CommandWardrobeSetColour(ModCommand parent) {
        super(parent, "set_colour");
    }

    @Override
    public LiteralArgumentBuilder buildCommand(){
        EnumArgument<ExtraColourType> extraColourTypeEnumArgument = EnumArgument.enumArgument(ExtraColourType.class);
        return literal(this.getName()).then(
                argument("player", EntityArgument.players()).then(
                        argument("extra_colour_type", extraColourTypeEnumArgument).then(
                                argument("dye", StringArgumentType.string()).executes((x)->this.execute(x))
                        )
                ));
    }


    // Arguments 3 - <player> <extra colour type> <dye>
    @Override
    public int execute(CommandContext ctx) throws CommandException, CommandSyntaxException {
        ServerPlayerEntity player = getPlayer(ctx, "player");
        ExtraColourType colourType = (ExtraColourType) ctx.getArgument("extra_colour_type", ExtraColourType.class);
        String argDye = getString(ctx, "dye");

        Color colour = null;

        if (argDye.startsWith("#") && argDye.length() == 7) {
            if (isValidHex(argDye)) {
                Color dyeColour = Color.decode(argDye);
                int r = dyeColour.getRed();
                int g = dyeColour.getGreen();
                int b = dyeColour.getBlue();
                colour = new Color(r, g, b, 255);
            } else {
                throw new CommandException(new StringTextComponent(getFullName() + ".invalidColourFormat" +argDye));
            }
        } else if (argDye.length() >= 5 & argDye.contains(",")) {
            String dyeValues[] = argDye.split(",");
            if (dyeValues.length != 3) {
                throw new CommandException(new StringTextComponent(getFullName() + ".badcolor" +argDye));
            }
            int r = parseInt(dyeValues[0], 0, 255);
            int g = parseInt(dyeValues[1], 0, 255);
            int b = parseInt(dyeValues[2], 0, 255);
            colour = new Color(r, g, b, 255);
        } else {
            throw new CommandException(new StringTextComponent(getFullName() + ".invalidColourFormat" +  argDye));
        }

        IPlayerWardrobeCap wardrobeCap = (IPlayerWardrobeCap) PlayerWardrobeCap.get(player);
        if (wardrobeCap != null) {
            wardrobeCap.getExtraColours().setColour(colourType, colour.getRGB());
            wardrobeCap.syncToPlayer(player);
            wardrobeCap.syncToAllTracking();
        }
        return 0;
    }

    private boolean isValidHex(String colorStr) {
        ModLogger.log(colorStr);
        String hexPatten = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Pattern pattern = Pattern.compile(hexPatten);
        Matcher matcher = pattern.matcher(colorStr);
        return matcher.matches();
    }
}
