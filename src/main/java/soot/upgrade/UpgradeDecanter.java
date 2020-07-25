package soot.upgrade;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import soot.block.BlockAlchemyGauge;
import soot.brewing.EssenceStack;
import soot.recipe.RecipeStill;
import soot.tile.TileEntityDecanterBottom;
import soot.tile.TileEntityStillBase;
import teamroots.embers.Embers;
import teamroots.embers.api.event.DialInformationEvent;
import teamroots.embers.api.event.MachineRecipeEvent;
import teamroots.embers.api.event.UpgradeEvent;
import teamroots.embers.util.DefaultUpgradeProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UpgradeDecanter extends DefaultUpgradeProvider {
    public UpgradeDecanter(TileEntity tile) {
        super("decanter", tile);
    }

    private static ThreadLocal<List<EssenceStack>> currentEssence = new ThreadLocal<>();
    RecipeStill currentRecipe;

    @Override
    public void throwEvent(TileEntity tile, UpgradeEvent event) {
        if(event instanceof DialInformationEvent) {
            DialInformationEvent dialEvent = (DialInformationEvent) event;
            if(BlockAlchemyGauge.DIAL_TYPE.equals(dialEvent.getDialType())) {
                dialEvent.getInformation().add(Embers.proxy.formatLocalize("embers.tooltip.upgrade.decanter")); //Proxy this because it runs in shared code
            }
        }
        if(event instanceof MachineRecipeEvent.Success) {
            if(this.tile instanceof TileEntityDecanterBottom) {
                List<EssenceStack> essence = currentEssence.get();
                if(essence == null) {
                    if(tile instanceof TileEntityStillBase) {
                        TileEntityStillBase still = (TileEntityStillBase) tile;
                        FluidStack input = still.tank.drain(currentRecipe.getInputConsumed(), false);
                        ItemStack catalyst = still.getTip().getCurrentCatalyst();
                        essence = new ArrayList<>(currentRecipe.getEssenceOutput(still, input, catalyst));
                    }
                    currentEssence.set(essence);
                }
                Iterator<EssenceStack> iterator = essence.iterator();
                while(iterator.hasNext()) {
                    EssenceStack myEssence = iterator.next();
                    if(((TileEntityDecanterBottom) this.tile).canAdd(myEssence)) {
                        ((TileEntityDecanterBottom) this.tile).add(myEssence);
                        iterator.remove();
                        break;
                    }
                }
            }
        } else if(event instanceof MachineRecipeEvent) {
            Object recipe = ((MachineRecipeEvent) event).getRecipe();
            if(recipe instanceof RecipeStill) {
                currentRecipe = (RecipeStill) recipe;
                currentEssence.remove();
            }
        }
    }
}
