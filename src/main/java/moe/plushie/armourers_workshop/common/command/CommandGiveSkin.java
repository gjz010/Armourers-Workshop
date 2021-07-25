package moe.plushie.armourers_workshop.common.command;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import moe.plushie.armourers_workshop.utils.UtilItems;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class CommandGiveSkin extends ModCommand {

    public CommandGiveSkin(ModCommand parent) {
        super(parent, "give_skin");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> buildCommand() {
        return literal(this.getName()).then(
                Commands.argument("player", EntityArgument.player()).then(
                        argument("skin_name", StringArgumentType.string()).then(
                                argument("dyes", StringArgumentType.greedyString()).executes((x)->this.execute(x))
                        )
                )
        );
    }

    @Override
    protected int execute(CommandContext ctx) throws CommandException, CommandSyntaxException {
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        String skinName = StringArgumentType.getString(ctx, "skin_name");
        String allDyes = StringArgumentType.getString(ctx, "dyes");
        String[] dyes = (String[]) Arrays.stream(allDyes.split(";")).filter((x)->x.length()>0).toArray();
        SkinDye skinDye = new SkinDye();
        for(String dyeCommand : dyes){
            ModLogger.log("Command dye: " + dyeCommand);

            if (!dyeCommand.contains("-")) {
                throw new CommandException(new StringTextComponent("bad color: "+dyeCommand));
            }
            String commandSplit[] = dyeCommand.split("-");
            if (commandSplit.length < 2 | commandSplit.length > 3) {
                throw new CommandException(new StringTextComponent("bad color: "+dyeCommand));
            }

            int dyeIndex = parseInt(commandSplit[0], 1, 8) - 1;
            String dye = commandSplit[1];
            IPaintType t = PaintTypeRegistry.PAINT_TYPE_NORMAL;
            if (commandSplit.length == 3) {
                String dyeString = commandSplit[2];
                t = PaintTypeRegistry.getInstance().getPaintTypeFormName(dyeString);
            }

            if (dye.startsWith("#") && dye.length() == 7) {
                // dye = dye.substring(2, 8);
                if (isValidHex(dye)) {
                    Color dyeColour = Color.decode(dye);
                    int r = dyeColour.getRed();
                    int g = dyeColour.getGreen();
                    int b = dyeColour.getBlue();
                    skinDye.addDye(dyeIndex, new byte[] { (byte) r, (byte) g, (byte) b, (byte) t.getId() });
                } else {
                    throw new CommandException(new StringTextComponent("commands.armourers.invalidDyeFormat: "+dye));
                }
            } else if (dye.length() >= 5 & dye.contains(",")) {
                String dyeValues[] = dye.split(",");
                if (dyeValues.length != 3) {
                    throw new CommandException(new StringTextComponent("bad color components: "+dyeCommand));
                }
                int r = parseInt(dyeValues[0], 0, 255);
                int g = parseInt(dyeValues[1], 0, 255);
                int b = parseInt(dyeValues[2], 0, 255);
                skinDye.addDye(dyeIndex, new byte[] { (byte) r, (byte) g, (byte) b, (byte) t.getId() });
            } else {
                throw new CommandException(new StringTextComponent("commands.armourers.invalidDyeFormat: "+dye));
            }
        }
        LibraryFile libraryFile = new LibraryFile(skinName);
        Skin skin = SkinIOUtils.loadSkinFromLibraryFile(libraryFile);
        if (skin == null) {
            throw new CommandException(new StringTextComponent("commands.armourers.fileNotFound: "+skinName));
        }
        try {
            skin.lightHash();
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, String.format("Unable to create ID for file %s.", libraryFile.toString()));
            return 1;
        }

        CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, libraryFile);
        SkinIdentifier skinIdentifier = new SkinIdentifier(0, libraryFile, 0, skin.getSkinType());
        ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(new SkinDescriptor(skinIdentifier, skinDye));

        if (!player.inventory.add(skinStack)) {
            UtilItems.spawnItemAtEntity(player, skinStack, true);
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
