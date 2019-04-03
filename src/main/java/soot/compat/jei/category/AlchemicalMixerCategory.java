package soot.compat.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import soot.Soot;
import soot.compat.jei.wrapper.AlchemicalMixerWrapper;
import soot.recipe.RecipeAlchemicalMixer;
import teamroots.embers.util.AspectRenderUtil;

import javax.annotation.Nonnull;
import java.util.List;

public class AlchemicalMixerCategory implements IRecipeCategory<AlchemicalMixerWrapper> {
    public static final int WIDTH = 108;
    public static final int HEIGHT = 125;
    public static final String UID = "soot.alchemical_mixer";
    public static final String L18N_KEY = "embers.jei.recipe.alchemical_mixer";
    @Nonnull
    private final IDrawable background;
    @Nonnull
    private final String localizedName;
    private RecipeAlchemicalMixer lastRecipe = null;
    private AspectRenderUtil helper;
    private final ResourceLocation resourceLocation = new ResourceLocation(Soot.MODID, "textures/gui/jei_alchemical_mixer.png");
    public static final int ASPECTBARS_X = 16;
    public static final int ASPECTBARS_Y = 69;

    public AlchemicalMixerCategory(IGuiHelper helper)
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
    public void setRecipe(IRecipeLayout recipeLayout, AlchemicalMixerWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluid = recipeLayout.getFluidStacks();
        fluid.init(0, true, 26, 3, 16, 16, 16, true, null);
        fluid.init(1, true, 26, 45, 16, 16, 16, true, null);
        fluid.init(2, true, 66, 3, 16, 16, 16, true, null);
        fluid.init(3, true, 66, 45, 16, 16, 16, true, null);
        fluid.init(4, false, 89, 16, 16, 32, 16, true, null);
        helper.addAspectStacks(recipeWrapper,stacks,0);
        //stacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> tooltip.clear());

        List<List<FluidStack>> inputs = ingredients.getInputs(FluidStack.class);
        int size = inputs.size();
        for(int i = 0; i < Math.min(size,4); i++)
        {
            fluid.set(i, inputs.get(i));
        }

        fluid.set(4, ingredients.getOutputs(FluidStack.class).get(0));

        lastRecipe = recipeWrapper.recipe;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        if(lastRecipe != null) {
            helper.drawAspectBars(minecraft,lastRecipe);
        }
    }
}
