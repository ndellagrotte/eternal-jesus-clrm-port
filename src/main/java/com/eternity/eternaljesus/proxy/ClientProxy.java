package com.eternity.eternaljesus.proxy;

import com.eternity.eternaljesus.client.ClientEventHandler;
import com.eternity.eternaljesus.client.GuiHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IProxy {

    @Override
    public void init() {
        GuiHandler.init();
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
}
