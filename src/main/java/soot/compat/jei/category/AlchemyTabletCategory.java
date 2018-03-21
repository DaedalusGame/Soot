package soot.compat.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import soot.Soot;
import soot.compat.jei.wrapper.AlchemicalMixerWrapper;
import soot.compat.jei.wrapper.AlchemyTabletWrapper;
import soot.recipe.RecipeAlchemyTablet;
import soot.util.AlchemyUtil;
import soot.util.AspectList;
import soot.util.AspectRenderUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AlchemyTabletCategory implements IRecipeCategory<AlchemyTabletWrapper> {
    public static final int WIDTH = 108;
    public static final int HEIGHT = 121;
    public static final String UID = "soot.alchemy_tablet";
    public static final String L18N_KEY = "embers.jei.recipe.alchemy";
    public static final int ASPECTBARS_X = 16;
    public static final int ASPECTBARS_Y = 64;
    @Nonnull
    private final IDrawable background;
    @Nonnull
    private final String localizedName;
    private final AspectRenderUtil helper;
    private RecipeAlchemyTablet lastRecipe = null;
    private final ResourceLocation resourceLocation = new ResourceLocation(Soot.MODID, "textures/gui/jei_alchemy.png");

    public AlchemyTabletCategory(IGuiHelper helper)
    {
        background = helper.createDrawable(resourceLocation, 0, 0, WIDTH, HEIGHT);
        localizedName = Translator.translateToLocal(L18N_KEY);
        this.helper = new AspectRenderUtil(helper,5,ASPECTBARS_X,ASPECTBARS_Y,108,0,54,7,resourceLocation);
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
    public void setRecipe(IRecipeLayout recipeLayout, AlchemyTabletWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        stacks.init(0, true, 27, 18);
        stacks.init(1, true, 9, 18);
        stacks.init(2, true, 27, 0);
        stacks.init(3, true, 45, 18);
        stacks.init(4, true, 27, 36);
        stacks.init(5, false, 81, 18);
        helper.addAspectStacks(recipeWrapper, stacks, 6);
        stacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {if(slotIndex >= 6) tooltip.clear();});

        for(int i = 0; i < 5; i++) {
            if(ingredients.getInputs(ItemStack.class).size() > i && ingredients.getInputs(ItemStack.class).get(i) != null) {
                stacks.set(i, (List)ingredients.getInputs(ItemStack.class).get(i));
            }
        }

        if(ingredients.getOutputs(ItemStack.class).size() > 0) {
            stacks.set(5, (List)ingredients.getOutputs(ItemStack.class).get(0));
        }

        lastRecipe = recipeWrapper.recipe;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        if(lastRecipe != null) {
            helper.drawAspectBars(minecraft, lastRecipe);
        }
    }


}
