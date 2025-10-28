package com.incompetent_modders.incomp_core.api.class_type.ability;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.incompetent_modders.incomp_core.common.util.Utils;
import com.incompetent_modders.incomp_core.core.network.serverbound.ClassAbilityPayload;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.util.*;

public class AbilityWheelOverlay implements LayeredDraw.Layer {
    public static AbilityWheelOverlay instance = new AbilityWheelOverlay();

    public final static ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, "textures/gui/icons.png");

    private final Vector4f lineColor = new Vector4f(1f, .85f, .7f, 1f);
    private final Vector4f radialButtonColor = new Vector4f(.04f, .03f, .01f, .6f);
    private final Vector4f highlightColor = new Vector4f(.8f, .7f, .55f, .7f);

    private final float ringInnerEdge = 20;
    private float ringOuterEdge = 80;
    private final float ringOuterEdgeMax = 80;
    private final float ringOuterEdgeMin = 65;


    public boolean active;
    private int wheelSelection;

    private List<AbilityEntry> abilities;
    private final int innerRadius = 50;
    private final int outerRadius = 110;

    public void open() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;
        ClassType classType = PlayerDataHelper.getClassType(localPlayer);
        if (classType.abilities().isEmpty()) return;
        findValidAbilities();
        active = true;
        wheelSelection = -1;
        Minecraft.getInstance().mouseHandler.releaseMouse();
    }

    public void findValidAbilities() {
        List<AbilityEntry> validAbilities = new ArrayList<>();
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            ClassType classType = PlayerDataHelper.getClassType(localPlayer);
            for (var entry : classType.getAbilities().entrySet()) {
                var ability = entry.getValue();
                if (ability.isCorrectLevel(0)) {
                    validAbilities.add(ability);
                }
            }
        }
        abilities = validAbilities;
    }

    public void close() {
        active = false;
        if (wheelSelection >= 0) {
            AbilityEntry abilityEntry = abilities.get(wheelSelection);
            if (abilityEntry != null) {
                PacketDistributor.sendToServer(new ClassAbilityPayload(abilityEntry.identifier(), true));
            }
        }
        Minecraft.getInstance().mouseHandler.grabMouse();
    }

    public void render(GuiGraphics guiHelper, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().options.hideGui || Minecraft.getInstance().player.isSpectator() || !active) {
            return;
        }
        var screenWidth = guiHelper.guiWidth();
        var screenHeight = guiHelper.guiHeight();

        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || minecraft.screen != null || minecraft.mouseHandler.isMouseGrabbed()) {
            close();
            return;
        }

        int abilitiesAvailable = abilities.size();

        if (abilitiesAvailable <= 0) {
            close();
            return;
        }

        PoseStack poseStack = guiHelper.pose();
        poseStack.pushPose();
        float guiScale = 1f;
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        poseStack.translate(centerX, centerY, 0);
        poseStack.scale(guiScale, guiScale, 1);
        centerX = 0;
        centerY = 0;

        Vec2 screenCenter = new Vec2(minecraft.getWindow().getScreenWidth() * .5f, minecraft.getWindow().getScreenHeight() * .5f);
        Vec2 mousePos = new Vec2((float) minecraft.mouseHandler.xpos(), (float) minecraft.mouseHandler.ypos());
        double radiansPerSpell = Math.toRadians(360 / (float) abilitiesAvailable);

        float mouseRotation = (Utils.getAngle(mousePos, screenCenter) + 1.570f + (float) radiansPerSpell * .5f) % 6.283f;

        wheelSelection = (int) Mth.clamp(mouseRotation / radiansPerSpell, 0, abilitiesAvailable - 1);
        if (mousePos.distanceToSqr(screenCenter) < ringOuterEdgeMin * ringOuterEdgeMin) {
            wheelSelection = Math.max(0, abilitiesAvailable -1);
        }

        guiHelper.fill(0, 0, screenWidth, screenHeight, 0);

        drawRadialBackgrounds(guiHelper, centerX, centerY, wheelSelection);
        drawDividingLines(guiHelper, centerX, centerY);

        var selectedAbility = abilities.get(wheelSelection);
        var font = Minecraft.getInstance().font;
        int textHeight = 3 * font.lineHeight + 5;
        int textCenterMargin = 5;
        int textTitleMargin = 5;
        var title = selectedAbility.getDisplayName(minecraft.player).withStyle(Style.EMPTY.withUnderlined(true));
        var cooldownTicks = selectedAbility.getEffectiveCooldown(0);
        var cooldownTime = Component.translatable("tooltip.incompetent_core.cooldown_length_seconds", Utils.timeFromTicks(cooldownTicks, 2)).withStyle(ChatFormatting.YELLOW);
        int infoHeight = (int) (centerY - (ringOuterEdge + textHeight) + font.lineHeight + textTitleMargin);
        drawTextBackground(guiHelper, centerX, centerY, ringOuterEdge + textHeight - textTitleMargin - font.lineHeight, textCenterMargin, 2 * font.lineHeight);
        guiHelper.drawString(font, title, (int) (centerX - font.width(title) / 2), (int) (centerY - (ringOuterEdge + textHeight)), 0xFFFFFF, true);
        if (cooldownTicks > 0) {
            infoHeight += font.lineHeight;
            guiHelper.drawString(font, cooldownTime, (int) (centerX - font.width(cooldownTime) - textCenterMargin), infoHeight, 0xFFFFFF, true);
        }

        //Ability Icons
        float scale = Mth.lerp(abilitiesAvailable / 15f, 2, 1.25f) * .65f;
        double radius = 3 / scale * (ringInnerEdge + ringInnerEdge) * .5 * (.85f + .25f * (abilitiesAvailable / 15f));
        Vec2[] locations = new Vec2[abilitiesAvailable];
        for (int i = 0; i < locations.length; i++) {
            locations[i] = new Vec2((float) (Math.sin(radiansPerSpell * i) * radius), (float) (-Math.cos(radiansPerSpell * i) * radius));
        }
        for (int i = 0; i < locations.length; i++) {
            var currentAbility = abilities.get(i);
            if (currentAbility != null) {
                var texture = currentAbility.getIconResource();
                poseStack.pushPose();
                poseStack.translate(centerX, centerY, 0);
                poseStack.scale(scale, scale, scale);
                //Icon
                int iconWidth = 16 / 2;
                int borderWidth = 32 / 2;
                int cdWidth = 16 / 2;
                guiHelper.blit(texture, (int) locations[i].x - iconWidth, (int) locations[i].y - iconWidth, 0, 0, 16, 16, 16, 16);
                /*
                Border
                 */
                guiHelper.blit(TEXTURE, (int) locations[i].x - borderWidth, (int) locations[i].y - borderWidth, wheelSelection == i ? 32 : 0, 106, 32, 32);
                /*
                Cooldown
                 */
                float f = PlayerDataHelper.getAbilityCooldownPercentage(player, currentAbility.identifier());
                if (f > 0) {
                    RenderSystem.enableBlend();
                    int pixels = (int) (16 * f + 1f);
                    guiHelper.blit(TEXTURE, (int) locations[i].x - cdWidth, (int) locations[i].y + cdWidth - pixels, 47, 87, 16, pixels);
                }
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    private void drawTextBackground(GuiGraphics guiHelper, float centerX, float centerY, float textYOffset, int textCenterMargin, int textHeight) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        centerY = centerY - textYOffset - 2;
        int heightMax = textHeight / 2 + 4;
        int heightMin = 0;
        int widthMax = 70;
        int widthMin = 0;

        widthMin = -1;
        widthMax = 1;

        final VertexConsumer vertexConsumer = guiHelper.bufferSource().getBuffer(RenderType.gui());
        Matrix4f m = guiHelper.pose().last().pose();

        vertexConsumer.addVertex(m, centerX + widthMin, centerY + heightMin + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), radialButtonColor.w());
        vertexConsumer.addVertex(m, centerX + widthMin, centerY + heightMax + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), 0);
        vertexConsumer.addVertex(m, centerX + widthMax, centerY + heightMax + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), 0);
        vertexConsumer.addVertex(m, centerX + widthMax, centerY + heightMin + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), radialButtonColor.w());
        vertexConsumer.addVertex(m, centerX + widthMin, centerY + heightMin, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), 0);
        vertexConsumer.addVertex(m, centerX + widthMin, centerY + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), radialButtonColor.w());
        vertexConsumer.addVertex(m, centerX + widthMax, centerY + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), radialButtonColor.w());
        vertexConsumer.addVertex(m, centerX + widthMax, centerY + heightMin, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), 0);

        RenderSystem.disableBlend();
    }

    private void drawRadialBackgrounds(GuiGraphics guiGraphics, float centerX, float centerY, int selectedSpellIndex) {
        float quarterCircle = Mth.HALF_PI;
        int abilitiesAvailable = abilities.size();
        int segments;
        if (abilitiesAvailable < 6) {
            segments = abilitiesAvailable % 2 == 1 ? 15 : 12;
        } else {
            segments = abilitiesAvailable * 2;
        }
        float radiansPerObject = 2 * Mth.PI / segments;
        float radiansPerSpell = 2 * Mth.PI / abilitiesAvailable;
        ringOuterEdge = Math.max(ringOuterEdgeMin, ringOuterEdgeMax);
        for (int i = 0; i < segments; i++) {
            final float beginRadians = i * radiansPerObject - (quarterCircle + (radiansPerSpell / 2));
            final float endRadians = (i + 1) * radiansPerObject - (quarterCircle + (radiansPerSpell / 2));

            final float x1m1 = Mth.cos(beginRadians) * ringInnerEdge;
            final float x2m1 = Mth.cos(endRadians) * ringInnerEdge;
            final float y1m1 = Mth.sin(beginRadians) * ringInnerEdge;
            final float y2m1 = Mth.sin(endRadians) * ringInnerEdge;

            final float x1m2 = Mth.cos(beginRadians) * ringOuterEdge;
            final float x2m2 = Mth.cos(endRadians) * ringOuterEdge;
            final float y1m2 = Mth.sin(beginRadians) * ringOuterEdge;
            final float y2m2 = Mth.sin(endRadians) * ringOuterEdge;

            boolean isHighlighted = (i * abilitiesAvailable) / segments == selectedSpellIndex;

            Vector4f color = radialButtonColor;
            if (isHighlighted) color = highlightColor;

            final VertexConsumer vertexConsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
            final Matrix4f m = guiGraphics.pose().last().pose();

            vertexConsumer.addVertex(m, centerX + x1m1, centerY + y1m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m1, centerY + y2m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m2, centerY + y2m2, 0).setColor(color.x(), color.y(), color.z(), 0);
            vertexConsumer.addVertex(m, centerX + x1m2, centerY + y1m2, 0).setColor(color.x(), color.y(), color.z(), 0);

            //Category line
            color = lineColor;
            float categoryLineWidth = 2;
            final float categoryLineOuterEdge = ringInnerEdge + categoryLineWidth;

            final float x1m3 = Mth.cos(beginRadians) * categoryLineOuterEdge;
            final float x2m3 = Mth.cos(endRadians) * categoryLineOuterEdge;
            final float y1m3 = Mth.sin(beginRadians) * categoryLineOuterEdge;
            final float y2m3 = Mth.sin(endRadians) * categoryLineOuterEdge;

            vertexConsumer.addVertex(m, centerX + x1m1, centerY + y1m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m1, centerY + y2m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m3, centerY + y2m3, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x1m3, centerY + y1m3, 0).setColor(color.x(), color.y(), color.z(), color.w());
        }
    }

    private void drawDividingLines(GuiGraphics guiHelper, float centerX, float centerY) {
        int abilitiesAvailable = abilities.size();

        if (abilitiesAvailable <= 1)
            return;

        float quarterCircle = Mth.HALF_PI;
        float radiansPerSpell = 2 * Mth.PI / abilitiesAvailable;
        ringOuterEdge = Math.max(ringOuterEdgeMin, ringOuterEdgeMax);

        for (int i = 0; i < abilitiesAvailable; i++) {
            final float closeWidth = 8 * Mth.DEG_TO_RAD;
            final float farWidth = closeWidth / 4;
            final float beginCloseRadians = i * radiansPerSpell - (quarterCircle + (radiansPerSpell / 2)) - (closeWidth / 4);
            final float endCloseRadians = beginCloseRadians + closeWidth;
            final float beginFarRadians = i * radiansPerSpell - (quarterCircle + (radiansPerSpell / 2)) - (farWidth / 4);
            final float endFarRadians = beginCloseRadians + farWidth;

            final float x1m1 = Mth.cos(beginCloseRadians) * ringInnerEdge;
            final float x2m1 = Mth.cos(endCloseRadians) * ringInnerEdge;
            final float y1m1 = Mth.sin(beginCloseRadians) * ringInnerEdge;
            final float y2m1 = Mth.sin(endCloseRadians) * ringInnerEdge;

            final float x1m2 = Mth.cos(beginFarRadians) * ringOuterEdge * 1.4f;
            final float x2m2 = Mth.cos(endFarRadians) * ringOuterEdge * 1.4f;
            final float y1m2 = Mth.sin(beginFarRadians) * ringOuterEdge * 1.4f;
            final float y2m2 = Mth.sin(endFarRadians) * ringOuterEdge * 1.4f;

            Vector4f color = lineColor;
            final VertexConsumer vertexConsumer = guiHelper.bufferSource().getBuffer(RenderType.gui());
            Matrix4f m = guiHelper.pose().last().pose();

            vertexConsumer.addVertex(m, centerX + x1m1, centerY + y1m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m1, centerY + y2m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m2, centerY + y2m2, 0).setColor(color.x(), color.y(), color.z(), 0);
            vertexConsumer.addVertex(m, centerX + x1m2, centerY + y1m2, 0).setColor(color.x(), color.y(), color.z(), 0);
        }
    }

    private void setOpaqueTexture(ResourceLocation texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, texture);
    }

    private void setTranslucentTexture(ResourceLocation texture) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getRendertypeTranslucentShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, texture);
    }

    private boolean inTriangle(final double x1, final double y1, final double x2, final double y2,
                               final double x3, final double y3, final double x, final double y) {
        final double ab = (x1 - x) * (y2 - y) - (x2 - x) * (y1 - y);
        final double bc = (x2 - x) * (y3 - y) - (x3 - x) * (y2 - y);
        final double ca = (x3 - x) * (y1 - y) - (x1 - x) * (y3 - y);
        return sign(ab) == sign(bc) && sign(bc) == sign(ca);
    }

    private int sign(final double n) {
        return n > 0 ? 1 : -1;
    }
}
