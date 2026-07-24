package com.eternity.eternaljesus.client;

import com.eternity.eternaljesus.Reference;
import com.eternity.eternaljesus.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Core of the gimmick: picks a random full-screen "Jesus" photo, plays the bell, and fades the
 * image out over {@link #DISPLAY_DURATION} ms. Ported from the source mod's {@code guihandler},
 * swapping the 1.20 {@code GuiGraphics}/{@code RenderSystem} pipeline for 1.12.2 {@code GlStateManager}
 * + {@link Gui#drawModalRectWithCustomSizedTexture}.
 */
public class GuiHandler {

    private static final int NUMBEROFIMAGES = 5;
    private static final int DISPLAY_DURATION = 500;
    private static final Random RANDOM = new Random();

    private static long startTime = -1;
    private static boolean shouldDisplay = false;

    public static ResourceLocation image_id = new ResourceLocation(Reference.MOD_ID, "textures/gui/jesus0.png");
    public static final List<ResourceLocation> images = new ArrayList<>();

    public static void init() {
        for (int i = 0; i < NUMBEROFIMAGES; i++) {
            images.add(new ResourceLocation(Reference.MOD_ID, "textures/gui/jesus" + i + ".png"));
        }
    }

    public static void triggerDisplay() {
        if (!shouldDisplay) {
            shouldDisplay = true;
            display();
        }
    }

    public static void display() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world != null && mc.player != null) {
            // Throttle: only start a fresh flash if none has started within the last 2x window.
            if (startTime == -1 || System.currentTimeMillis() - startTime > DISPLAY_DURATION * 2L) {
                if (!images.isEmpty()) {
                    image_id = images.get(RANDOM.nextInt(images.size()));
                } else {
                    System.err.println("Error: images list is empty.");
                    return;
                }
                startTime = System.currentTimeMillis();
                playLocalSound(ModSounds.JESUS_BELL, mc.player);
            }
        }
    }

    public static void playLocalSound(SoundEvent soundEvent, EntityPlayer player) {
        if (soundEvent == null) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        // Client-only World#playSound overload; schedule onto the client thread to be safe.
        mc.addScheduledTask(() -> mc.world.playSound(
                player.posX, player.posY, player.posZ,
                soundEvent, SoundCategory.MASTER,
                30.0F, 1.0F, false));
    }

    public static void render(ScaledResolution resolution) {
        if (!shouldDisplay || startTime < 0 || Minecraft.getMinecraft().world == null) {
            return;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= DISPLAY_DURATION) {
            startTime = -1;
            shouldDisplay = false;
            return;
        }

        int screenWidth = resolution.getScaledWidth();
        int screenHeight = resolution.getScaledHeight();
        float opacity = 1.0F - (float) elapsedTime / DISPLAY_DURATION;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, opacity);

        Minecraft.getMinecraft().getTextureManager().bindTexture(image_id);
        // Stretch the full texture (u=v=0, texW/texH == draw size) across the whole screen.
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }
}
