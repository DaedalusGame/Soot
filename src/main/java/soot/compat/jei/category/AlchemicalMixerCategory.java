package soot.compat.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import soot.Soot;
import soot.compat.jei.wrapper.AlchemicalMixerWrapper;
import soot.recipe.RecipeAlchemicalMixer;
import soot.util.AspectList;

import javax.annotation.Nonnull;
import java.util.List;

public class AlchemicalMixerCategory implements IRecipeCategory<AlchemicalMixerWrapper> {
    public static final int WIDTH = 108;
    public static final int HEIGHT = 125;
    public static final String UID = "soot.alchemical_mixer";
    public static final String L18N_KEY = "embers.jei.recipe.dawnstone_anvil";
    @Nonnull
    private final IDrawable background;
    @Nonnull
    private final String localizedName;
    private RecipeAlchemicalMixer lastRecipe = null;
    private IGuiHelper helper;
    private final ResourceLocation resourceLocation = new ResourceLocation(Soot.MODID, "textures/gui/jei_alchemical_mixer.png");

    public AlchemicalMixerCategory(IGuiHelper helper)
    {
        background = helper.createDrawable(resourceLocation, 0, 0, WIDTH, HEIGHT);
        localizedName = Translator.translateToLocal(L18N_KEY);
        this.helper = helper;
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
        IGuiFluidStackGroup fluid = recipeLayout.getFluidStacks();
        fluid.init(0, true, 26, 3, 16, 16, 16, true, null);
        fluid.init(1, true, 26, 45, 16, 16, 16, true, null);
        fluid.init(2, true, 66, 3, 16, 16, 16, true, null);
        fluid.init(3, true, 66, 45, 16, 16, 16, true, null);
        fluid.init(4, false, 89, 16, 16, 32, 16, true, null);

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
            AspectList.AspectRangeList aspectRange = lastRecipe.aspectRange;
            int aspectTotal = aspectRange.getMaxAspects().getTotal();
            drawAspectBar(minecraft, aspectRange, aspectTotal, 16, 69, "iron");
            drawAspectBar(minecraft, aspectRange, aspectTotal, 16, 80, "copper");
            drawAspectBar(minecraft, aspectRange, aspectTotal, 16, 91, "dawnstone");
            drawAspectBar(minecraft, aspectRange, aspectTotal, 16, 102, "lead");
            drawAspectBar(minecraft, aspectRange, aspectTotal, 16, 114, "silver");
        }
    }

    public void drawAspectBar(Minecraft minecraft, AspectList.AspectRangeList aspectRange, int aspectTotal, int x, int y, String aspect) {
        int max = aspectRange.getMax(aspect);
        if (max > 0){
            int min = aspectRange.getMin(aspect);
            int u = 109;
            int v = 0;
            int width = 54;
            int height = 7;
            IDrawable ashBar = helper.createDrawable(resourceLocation, u, v, ((width *min)/aspectTotal), height);
            IDrawable ashPartialBar = helper.createDrawable(resourceLocation, u, v + 7, ((width * max)/aspectTotal), height);
            ashPartialBar.draw(minecraft, x, y);
            ashBar.draw(minecraft, x, y);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(min+"-"+max, x +width+6, y, 0xFFFFFF);
        }
    }
}
