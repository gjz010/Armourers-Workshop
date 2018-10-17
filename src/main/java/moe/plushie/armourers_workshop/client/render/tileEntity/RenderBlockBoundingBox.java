package moe.plushie.armourers_workshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.client.render.IRenderBuffer;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.RenderBridge;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityBoundingBox;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;

public class RenderBlockBoundingBox extends TileEntitySpecialRenderer<TileEntityBoundingBox> {

    private final IRenderBuffer renderer;
    
    public RenderBlockBoundingBox() {
        renderer = RenderBridge.INSTANCE;
    }
    
    @Override
    public void render(TileEntityBoundingBox te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        //ModLogger.log(te.isParentValid());
        if (!(te.getSkinPart() instanceof ISkinPartTypeTextured)) {
            return;
        }
        RenderBlockColourable.updateAlpha(te);
        if (!(RenderBlockColourable.markerAlpha > 0)) {
            return;
        }
        
        //ModRenderHelper.disableLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        ModRenderHelper.enableAlphaBlend();
        GL11.glColor4f(1F, 1F, 1F, RenderBlockColourable.markerAlpha);
        bindTexture(RenderBlockColourable.MARKERS);
        renderer.startDrawingQuads(DefaultVertexFormats.POSITION_TEX);
        for (int i = 0; i < 6; i++) {
            if (te.isPaintableSide(i)) {
                EnumFacing dir = EnumFacing.byIndex(i);
                PaintType paintType = te.getPaintType(dir);
                if (paintType != PaintType.NONE) {
                    RenderBlockColourable.renderFaceWithMarker(renderer, x, y, z, dir, paintType.getMarkerId());
                }
            }
        }
        renderer.draw();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        ModRenderHelper.disableAlphaBlend();
        ModRenderHelper.enableLighting();
        RenderHelper.enableStandardItemLighting();
    }
}
