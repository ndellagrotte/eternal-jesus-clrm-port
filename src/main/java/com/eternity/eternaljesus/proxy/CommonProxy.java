package com.eternity.eternaljesus.proxy;

public class CommonProxy implements IProxy {

    @Override
    public void init() {
        // Nothing to do on the dedicated server: this mod is a purely client-side visual gimmick.
    }
}
