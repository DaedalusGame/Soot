package soot.compat.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.util.Translator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import soot.Soot;
import soot.compat.jei.wrapper.StillWrapper;

import javax.annotation.Nonnull;
import java.util.List;

public class StillCategory implements IRecipeCategory<StillWrapper> {
    public static final int WIDTH = 73;
    public static final int HEIGHT = 71;
    public static final String UID = "embers.still";
    public static final String L18N_KEY = "embers.jei.recipe.still";
    @Nonnull
    private final IDrawable background;
    @Nonnull
    private final String localizedName;

    public StillCategory(IGuiHelper helper)
    {
        ResourceLocation location = new ResourceLocation(Soot.MODID, "textures/gui/jei_still.png");
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
    public void setRecipe(IRecipeLayout recipeLayout, StillWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluid = recipeLayout.getFluidStacks();
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        fluid.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if(ingredient != null) {
                tooltip.remove(1);
                tooltip.add(1,Translator.translateToLocalFormatted("jei.tooltip.liquid.amount", ingredient.amount));
            }
            if(slotIndex == 1)
                recipeWrapper.modifyTooltip(tooltip);
        });
        fluid.init(0, true, 8, 28, 16, 16, 1, true, null);
        fluid.init(1, true, 49, 28, 16, 16, 1, true, null);
        fluid.set(0,ingredients.getInputs(FluidStack.class).get(0));
        fluid.set(1,ingredients.getOutputs(FluidStack.class).get(0));
        stacks.init(0,true,48,3);
        List<List<ItemStack>> outputStacks = ingredients.getInputs(ItemStack.class);
        if(outputStacks.size() > 0)
            stacks.set(0,outputStacks.get(0));
    }
}
