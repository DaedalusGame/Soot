package soot.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RecipeRegistry {
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(RecipeRegistry.class);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {

    }
}
