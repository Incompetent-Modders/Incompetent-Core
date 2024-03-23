package com.incompetent_modders.incomp_core.api.client.renderer;

import com.incompetent_modders.incomp_core.api.client.model.SpellProjectleModel;
import com.incompetent_modders.incomp_core.api.entity.projectile.SpellProjectile;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.awt.*;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;
@OnlyIn(Dist.CLIENT)
public class SpellProjectileRenderer extends EntityRenderer<SpellProjectile> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/projectiles/spell_projectile.png");
    private final SpellProjectleModel model;
    
    public SpellProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new SpellProjectleModel(pContext.bakeLayer(SpellProjectleModel.LAYER_LOCATION));
    }
    
    public void render(SpellProjectile pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        Color color = pEntity.getColor();
        float colorRed = color.getRed();
        float colorGreen = color.getGreen();
        float colorBlue = color.getBlue();
        float colorAlpha = 1F;
        GlStateManager._clearColor(colorRed, colorGreen, colorBlue, colorAlpha);
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }
    
    //protected float xOffset(float pTickCount) {
    //    return pTickCount * 0.03F;
    //}
    
    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(SpellProjectile pEntity) {
        return TEXTURE_LOCATION;
    }
}
