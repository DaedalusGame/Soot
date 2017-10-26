package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;
import soot.Soot;
import soot.tile.TileEntityMixerBottomImproved;
import teamroots.embers.block.BlockMixer;
import teamroots.embers.tileentity.TileEntityMixerTop;

import javax.annotation.Nullable;

public class BlockMixerImproved extends BlockMixer {
    public BlockMixerImproved(Material material, String name, boolean addToTab) {
        super(material, name, addToTab);
        try {
            ReflectionHelper.findField(IForgeRegistryEntry.Impl.class,"registryName").set(this,new ResourceLocation(Soot.MODID,name));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if(meta == 1)
            return new TileEntityMixerTop();
        else
            return new TileEntityMixerBottomImproved();
    }
}
