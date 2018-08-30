package soot.upgrade;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import soot.block.BlockAlchemyGauge;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeStill;
import soot.recipe.RecipeStillDoubleDistillation;
import soot.recipe.RecipeStillModifier;
import soot.tile.TileEntityStillBase;
import soot.tile.TileEntityStillTip;
import teamroots.embers.Embers;
import teamroots.embers.api.event.DialInformationEvent;
import teamroots.embers.api.event.UpgradeEvent;
import teamroots.embers.api.tile.IMechanicallyPowered;
import teamroots.embers.block.BlockEmberGauge;
import teamroots.embers.util.DefaultUpgradeProvider;

public class UpgradeDistillationPipe extends DefaultUpgradeProvider {
    public UpgradeDistillationPipe(TileEntity tile) {
        super("distillation_pipe", tile);
    }

    @Override
    public double getSpeed(TileEntity tile, double speed) {
        if(tile instanceof TileEntityStillBase) { //TODO: Optimize
            TileEntityStillBase base = (TileEntityStillBase) tile;
            TileEntityStillTip tip = base.getTip();
            if(tip != null) {
                FluidStack inputStack = base.tank.getFluid();
                RecipeStill recipe = CraftingRegistry.getStillRecipe(base, inputStack, tip.getCurrentCatalyst());
                if(isDistillationRecipe(recipe))
                    return speed + 0.5;
            }
        }

        return speed;
    }

    private boolean isDistillationRecipe(RecipeStill recipe) {
        return !(recipe instanceof RecipeStillModifier) || recipe.catalystInput.apply(ItemStack.EMPTY);
    }

    @Override
    public boolean getOtherParameter(TileEntity tile, String type, boolean value) {
        if(type.equals(RecipeStillDoubleDistillation.TAG_DOUBLE_DISTILL))
            return true; //Enable double distillation.
        return value;
    }

    @Override
    public void throwEvent(TileEntity tile, UpgradeEvent event) {
        if(event instanceof DialInformationEvent) {
            DialInformationEvent dialEvent = (DialInformationEvent) event;
            if(BlockAlchemyGauge.DIAL_TYPE.equals(dialEvent.getDialType())) {
                dialEvent.getInformation().add(Embers.proxy.formatLocalize("embers.tooltip.upgrade.distillation_pipe")); //Proxy this because it runs in shared code
            }
        }
    }
}
