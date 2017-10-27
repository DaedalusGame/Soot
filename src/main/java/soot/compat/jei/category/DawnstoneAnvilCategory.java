package soot.compat.jei.category;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.util.Translator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import soot.Soot;
import soot.compat.jei.wrapper.DawnstoneAnvilWrapper;
import teamroots.embers.RegistryManager;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class DawnstoneAnvilCategory implements IRecipeCategory<DawnstoneAnvilWrapper> {
    public static final int WIDTH = 109;
    public static final int HEIGHT = 57;
    public static final String UID = "embers.dawnstone_anvil";
    public static final String L18N_KEY = "embers.jei.recipe.dawnstone_anvil";
    @Nonnull
    private final IDrawable background;
    @Nonnull
    private final String localizedName;

    public DawnstoneAnvilCategory(IGuiHelper helper)
    {
        ResourceLocation location = new ResourceLocation(Soot.MODID, "textures/gui/jei_dawnstone_anvil.png");
        background = helper.createDrawable(location, 0, 0, WIDTH, HEIGHT);
        localizedName = Translator.translateToLocal(L18N_KEY);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public String getModName() {
        return Soot.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, DawnstoneAnvilWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0,true, 21, 18);
        guiItemStacks.init(1,true, 21, 36);
        guiItemStacks.init(2,false, 76, 27);
        guiItemStacks.init(3,false, 21, 0); //The hammer

        guiItemStacks.set(0,ingredients.getInputs(ItemStack.class).get(0));
        guiItemStacks.set(1,ingredients.getInputs(ItemStack.class).get(1));
        guiItemStacks.set(2,ingredients.getOutputs(ItemStack.class).get(0));
        guiItemStacks.set(3, Arrays.asList(new ItemStack(RegistryManager.tinker_hammer),new ItemStack(RegistryManager.auto_hammer)));
    }
}
