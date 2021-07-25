package moe.plushie.armourers_workshop.common;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import org.apache.commons.io.IOUtils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public final class TextureHelper {
    /*
     * Based on @KitsuneKihira texture helper class.
     * https://github.com/kihira/FoxLib/blob/2946cd6033d3039151064ceccfb8d38612d0af02/src/main/scala/kihira/foxlib/client/TextureHelper.scala#L28
     */
    public static NativeImage getNativeImage(DownloadingTexture texture){
        NativeImage image = ObfuscationReflectionHelper.getPrivateValue(DownloadingTexture.class, texture, )
    }
    public static NativeImage getBufferedImageSkin(AbstractClientPlayerEntity player) {
        NativeImage bufferedImage = null;
        ResourceLocation skinloc = DefaultPlayerSkin.getDefaultSkin();
        InputStream inputStream = null;
        Minecraft mc = Minecraft.getInstance();
        skinloc = player.getSkinTextureLocation();
        return getBufferedImageSkin(skinloc);
    }
    
    public static NativeImage getBufferedImageSkin(ResourceLocation resourceLocation) {
        Minecraft mc = Minecraft.getInstance();
        NativeImage bufferedImage = null;
        InputStream inputStream = null;
        
        try {
            Texture skintex = mc.getTextureManager().getTexture(resourceLocation);
            if (skintex instanceof DownloadingTexture) {
                DownloadingTexture imageData = (DownloadingTexture) skintex;
                //bufferedImage  = ObfuscationReflectionHelper.getPrivateValue(.class, imageData, "bufferedImage", "field_110560_d", "bpr.h");
            } else {
                inputStream = mc.getResourceManager().getResource(resourceLocation).getInputStream();
                bufferedImage = NativeImage.read(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        
        return bufferedImage;
    }
    
    public static NativeImage getBufferedImageSkin(GameProfile gameProfile) {
        NativeImage bufferedImage = null;
        ResourceLocation skinloc = DefaultPlayerSkin.getDefaultSkin();
        InputStream inputStream = null;
        Minecraft mc = Minecraft.getInstance();
        Map map = mc.getSkinManager().getInsecureSkinInformation(gameProfile);
        
        try {
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                skinloc = mc.getSkinManager().registerTexture((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
                Texture skintex = mc.getTextureManager().getTexture(skinloc);
                if (skintex instanceof DownloadingTexture) {
                    DownloadingTexture imageData = (DownloadingTexture) skintex;
                    bufferedImage = imageData.getPixels();
               } else {
                    inputStream = mc.getResourceManager().getResource(skinloc).getInputStream();
                    bufferedImage = NativeImage.read(inputStream);
                }
            } else {
                inputStream = mc.getResourceManager().getResource(skinloc).getInputStream();
                bufferedImage = NativeImage.read(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        
        return bufferedImage;
    }
    
    public static NativeImage getBufferedImageSkinNew(GameProfile gameProfile) {
        NativeImage bufferedImage = null;
        ResourceLocation rl = DefaultPlayerSkin.getDefaultSkin();
        
        if (gameProfile != null) {
            rl = AbstractClientPlayerEntity.getSkinLocation(gameProfile.getName());
            AbstractClientPlayerEntity.registerSkinTexture(rl, gameProfile.getName());
        }
        bufferedImage = getBuffFromResourceLocation(rl);
        
        if (bufferedImage == null) {
            bufferedImage = getBuffFromResourceLocation(DefaultPlayerSkin.getDefaultSkin());
        }
        return bufferedImage;
    }
    
    private static NativeImage getBuffFromResourceLocation(ResourceLocation resourceLocation) {
        NativeImage bufferedImage = null;
        InputStream inputStream = null;
        Minecraft mc = Minecraft.getInstance();
        try {
            Texture skintex = mc.getTextureManager().getTexture(resourceLocation);
            if (skintex instanceof DownloadingTexture) {
                DownloadingTexture imageData = (DownloadingTexture) skintex;
                bufferedImage = imageData.getPixels();
            } else {
                inputStream = mc.getResourceManager().getResource(resourceLocation).getInputStream();
                bufferedImage = NativeImage.read(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return bi;
    }
    
    public static void bindPlayersNormalSkin(GameProfile gameProfile) {
        ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkin();
        if (gameProfile != null) {
            resourcelocation = getSkinResourceLocation(gameProfile, MinecraftProfileTexture.Type.SKIN);
        }
        Minecraft.getInstance().renderEngine.bindTexture(resourcelocation);
    }
    
    public static ResourceLocation getSkinResourceLocation(GameProfile gameProfile, MinecraftProfileTexture.Type type) {
        ResourceLocation skin = DefaultPlayerSkin.getDefaultSkin();
        if (gameProfile != null) {
            Minecraft mc = Minecraft.getInstance();
            Map<?, ?> map = mc.getSkinManager().getInsecureSkinInformation(gameProfile);
            if (map.containsKey(type)) {
                skin = mc.getSkinManager().registerTexture((MinecraftProfileTexture)map.get(type), type);
            }
        }
        return skin;
    }
    
    public static NativeImage deepCopyBufferedImage(NativeImage bufferedImage) {
        net.minecraftforge.client.MinecraftForgeClient;
        NativeImage.PixelFormat cm = bufferedImage.format();
        boolean isAlphaPremultiplied = cm
        WritableRaster raster = bufferedImage.copyData(null);
        return new NativeImage()
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
