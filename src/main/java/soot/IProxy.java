package soot;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public interface IProxy {
    void preInit();

    void init();

    void postInit();

    void registerBlockModel(Block block);

    void registerItemModel(Item item);
}
