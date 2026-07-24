package com.eternity.eternaljesus.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Client-side glue. Replaces the source mod's {@code LocalPlayer#hurt} Mixin with a client-tick
 * health-delta watcher (the mixin target is invalid on 1.12.2, and this is more robust across
 * single/multiplayer since the client's health is always synced), plus the HUD overlay renderer
 * that draws the flash on top of everything.
 */
public class ClientEventHandler {

    private float prevHealth = 20.0F;
    private boolean primed = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) {
            // Left the world: re-baseline so rejoining at low health doesn't false-trigger.
            primed = false;
            return;
        }
        float health = player.getHealth();
        if (!primed) {
            prevHealth = health;
            primed = true;
            return;
        }
        if (health < prevHealth && health <= 3.0F
                && player.isEntityAlive() && !player.capabilities.isCreativeMode) {
            GuiHandler.triggerDisplay();
        }
        prevHealth = health;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        // ElementType.ALL fires once, after the whole HUD, so the flash draws on top of it.
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            GuiHandler.render(new ScaledResolution(Minecraft.getMinecraft()));
        }
    }
}
