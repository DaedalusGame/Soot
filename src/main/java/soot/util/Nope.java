package soot.util;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.core.Logger;

import java.lang.reflect.Field;

public class Nope {
    /**
     * Make forge not spew "dangerous alternative prefix" messages in this block.
     */
    public static void shutupForge(Runnable op) {
        Logger log = (Logger) FMLLog.log;
        try {
            Object privateConfig = ReflectionHelper.findField(Logger.class, "privateConfig").get(log);
            Field intLevelF = ReflectionHelper.findField(privateConfig.getClass(), "intLevel");
            int intLevel = (int) intLevelF.get(privateConfig);
            intLevelF.set(privateConfig, 299); // disable WARN logging

            try {
                op.run();
            } finally {
                intLevelF.set(privateConfig, intLevel);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}