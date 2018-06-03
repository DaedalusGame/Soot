package soot.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import soot.util.OreTransmutationManager;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass(OreTransmutation.clazz)
public class OreTransmutation {
    public static final String clazz = "mods.soot.OreTransmutation";

    OreTransmutationManager.TransmutationSet internal;
}
