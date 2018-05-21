package soot;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public interface IProxy {
    void preInit();

    void init();

    void postInit();

    void registerBlockModel(Block block);

    void registerItemModel(Item item);

    void registerResourcePack();

    void addResourceOverride(String space, String dir, String file, String ext);

    void addResourceOverride(String modid, String space, String dir, String file, String ext);
}
