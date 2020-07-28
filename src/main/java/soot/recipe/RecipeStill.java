package soot.recipe;

import com.google.common.collect.Lists;
import mezz.jei.util.Translator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import soot.Registry;
import soot.brewing.EssenceStack;
import soot.brewing.EssenceType;
import soot.compat.jei.ExtraRecipeInfo;
import soot.item.ItemEssence;
import soot.tile.TileEntityStillBase;

import java.util.ArrayList;
import java.util.List;

public class RecipeStill {
    public ResourceLocation id;

    public FluidStack input;
    public Ingredient catalystInput;
    public int catalystConsumed;
    public FluidStack output;
    public List<EssenceStack> essence = new ArrayList<>();
    boolean exactMatch = false;

    public RecipeStill(ResourceLocation id, FluidStack input, Ingredient catalystInput, int catalystConsumed, FluidStack output) {
        this.id = id;
        this.input = input;
        this.catalystInput = catalystInput;
        this.output = output;
        this.catalystConsumed = catalystConsumed;
    }

    public RecipeStill setEssence(List<EssenceStack> essence) {
        this.essence = essence;
        return this;
    }

    public RecipeStill setExact() {
        exactMatch = true;
        return this;
    }

    public List<EssenceStack> getEssences() {
        return essence;
    }

    public List<EssenceStack> getEssenceOutput(TileEntityStillBase tile, FluidStack input, ItemStack catalyst) {
        return essence;
    }

    public List<FluidStack> getInputs()
    {
        return Lists.newArrayList(input);
    }

    public List<FluidStack> getOutputs()
    {
        return Lists.newArrayList(output);
    }

    public List<ItemStack> getEssenceItems() {
        List<ItemStack> stacks = new ArrayList<>();
        for (EssenceStack stack : essence) {
            stacks.add(Registry.ESSENCE.getStack(stack.getEssence()));
        }
        return stacks;
    }

    public ArrayList<ItemStack> getCatalysts() {
        return Lists.newArrayList(catalystInput.getMatchingStacks());
    }

    public void modifyTooltip(List<String> tooltip) {
        //NOOP
    }

    public int getInputConsumed()
    {
        return input != null ? input.amount : 0;
    }

    public boolean matches(TileEntityStillBase tile, FluidStack stack, ItemStack catalyst) {
        return catalystInput.apply(catalyst) && (input == null || (stack != null && (exactMatch ? input.isFluidEqual(stack) : input.getFluid() == stack.getFluid()) && stack.amount >= getInputConsumed()));
    }

    public FluidStack getOutput(TileEntityStillBase tile, FluidStack input)
    {
        return output.copy();
    }

    public List<ExtraRecipeInfo> getExtraInfo() {
        ArrayList<ExtraRecipeInfo> extraInfo = Lists.newArrayList();
        addEssenceInfo(extraInfo);
        return extraInfo;
    }

    private void addEssenceInfo(ArrayList<ExtraRecipeInfo> extraInfo) {
        for (EssenceStack stack : getEssences()) {
            EssenceType type = stack.getEssence();
            int amount = stack.getAmount();
            ItemStack essenceItem = Registry.ESSENCE.getStack(type);

            extraInfo.add(new ExtraRecipeInfo(Lists.newArrayList(essenceItem)) {
                @Override
                public void modifyTooltip(List<String> strings) {
                    String essenceName = Translator.translateToLocalFormatted("distilling.essence." + type.getName() + ".name");

                    strings.clear();
                    strings.add(Translator.translateToLocalFormatted("distilling.effect.decanter"));
                    strings.add(Translator.translateToLocalFormatted("distilling.effect.decanter.desc"));
                    strings.add(Translator.translateToLocalFormatted("distilling.effect.decanter.essence",essenceName,amount));
                }
            });
        }
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
