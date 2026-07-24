package com.eternity.eternaljesus.registry;

import com.eternity.eternaljesus.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * 1.12.2 sound registration. Replaces the source mod's {@code DeferredRegister<SoundEvent>}:
 * on 1.12.2 registry events fire on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS},
 * which {@code @Mod.EventBusSubscriber} subscribes to by default.
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModSounds {

    @GameRegistry.ObjectHolder("eternaljesus:jesus_bell")
    public static SoundEvent JESUS_BELL;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        ResourceLocation rl = new ResourceLocation(Reference.MOD_ID, "jesus_bell");
        event.getRegistry().register(new SoundEvent(rl).setRegistryName(rl));
    }
}
