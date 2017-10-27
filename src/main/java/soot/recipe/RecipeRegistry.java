package soot.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class RecipeRegistry {
    public static ArrayList<RecipeDawnstoneAnvil> dawnstoneAnvilRecipes = new ArrayList<>();

    public static void preInit()
    {
        MinecraftForge.EVENT_BUS.register(RecipeRegistry.class);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
    }

    public static RecipeDawnstoneAnvil getDawnstoneAnvilRecipe(ItemStack bottom, ItemStack top)
    {
        for (RecipeDawnstoneAnvil recipe: dawnstoneAnvilRecipes) {
            if(recipe.matches(bottom,top))
                return recipe;
        }

        return null;
    }
}
