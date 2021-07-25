package moe.plushie.armourers_workshop.common.command;

import java.io.File;
import java.util.List;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.exporter.ISkinExporter;
import moe.plushie.armourers_workshop.common.skin.exporter.SkinExportManager;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class CommandExportSkin extends ModCommand {

    public CommandExportSkin(ModCommand parent) {
        super(parent, "export_skin");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> buildCommand() {
        return Commands.literal(this.getName()).then(
                Commands.argument("extension", StringArgumentType.string()).then(
                        Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("scale", FloatArgumentType.floatArg())
                                        .executes((x)->this.executeWithScale(x, getFloat(x, "scale"))))
                                .executes((x)->this.executeWithScale(x, 0.0625f))
                ));
    }

    protected int executeWithScale(CommandContext<CommandSource> source, float scale) throws CommandException, CommandSyntaxException {

        ServerPlayerEntity player = source.getSource().getPlayerOrException();

        // Check if the player is holding a valid skin.
        ItemStack stack = player.getMainHandItem();
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (skinPointer == null) {
            throw new CommandException(new StringTextComponent("bad hand item"));
        }

        // Get export file extension.
        String fileExtension = getString(source, "extension");
        ISkinExporter skinExporter = SkinExportManager.getSkinExporter(fileExtension);
        if (skinExporter == null) {
            throw new CommandException(new StringTextComponent("bad extension"));
        }

        // Get export file name.
        String exportName = getString(source, "name");

        // Add the scale
        // Get the skin from the cache.
        // TODO Fix client call.
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            throw new CommandException(new StringTextComponent("bad skin"));
        }

        // Creating the export directory.
        File exportDir = new File(ArmourersWorkshop.getProxy().getModDirectory(), "model-exports");
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }

        SkinExportManager.exportSkin(skin, skinExporter, exportDir, exportName, scale);
        return 0;
    }
}
