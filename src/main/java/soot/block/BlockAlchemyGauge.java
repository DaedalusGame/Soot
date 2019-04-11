package soot.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import soot.tile.TileEntityAlchemyGauge;
import soot.brewing.FluidModifier;
import soot.util.FluidUtil;
import teamroots.embers.block.BlockBaseGauge;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BlockAlchemyGauge extends BlockBaseGauge {
    public static final String DIAL_TYPE = "alchemy";

    public BlockAlchemyGauge(Material blockMaterialIn) {
        super(blockMaterialIn);
        setIsFullCube(false);
        setIsOpaqueCube(false);
        setHarvestProperties("pickaxe", 0);
        setHardness(1.0f);
    }

    @Override
    protected void getTEData(EnumFacing facing, ArrayList<String> text, TileEntity tile) {
        if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing)){
            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
            if (handler != null){
                IFluidTankProperties[] properties = handler.getTankProperties();
                for (IFluidTankProperties property : properties) {
                    FluidStack contents = property.getContents();
                    if (contents != null) {
                        fillBrewData(text, contents);
                    }
                }
            }
        }
    }

    public void fillBrewData(ArrayList<String> text, FluidStack contents) {
        Fluid fluid = contents.getFluid();
        text.add(TextFormatting.BOLD+fluid.getLocalizedName(contents));
        NBTTagCompound compound = FluidUtil.getModifiers(contents);
        for(String key : FluidUtil.SORTED_MODIFIER_KEYS) {
            FluidModifier modifier = FluidUtil.MODIFIERS.get(key);
            if(!modifier.isDefault(compound, fluid)) {
                String formattedText = modifier.getFormattedText(compound, fluid);
                if(!formattedText.isEmpty())
                    text.add(formattedText);
            }
        }
    }

    @Override
    public String getDialType() {
        return DIAL_TYPE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityAlchemyGauge();
    }
}
