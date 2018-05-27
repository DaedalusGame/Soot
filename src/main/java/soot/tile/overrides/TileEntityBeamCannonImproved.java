package soot.tile.overrides;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import soot.SoundEvents;
import soot.util.ISparkable;
import teamroots.embers.RegistryManager;
import teamroots.embers.block.BlockBeamCannon;
import teamroots.embers.network.PacketHandler;
import teamroots.embers.network.message.MessageBeamCannonFX;
import teamroots.embers.power.EmberCapabilityProvider;
import teamroots.embers.power.IEmberPacketReceiver;
import teamroots.embers.tileentity.TileEntityAlchemyTablet;
import teamroots.embers.tileentity.TileEntityBeamCannon;

import java.util.List;

public class TileEntityBeamCannonImproved extends TileEntityBeamCannon {
    @Override
    public void update() {
        if (this.target == null && this.ticksExisted == 0){
            IBlockState state = getWorld().getBlockState(getPos());
            this.target = getPos().offset(state.getValue(BlockBeamCannon.facing));
        }
        ticksExisted ++;
        if (this.capability.getEmber() >= 400 && getWorld().isBlockIndirectlyGettingPowered(getPos()) != 0){
            Vec3d ray = (new Vec3d(target.getX()-getPos().getX(),target.getY()-getPos().getY(),target.getZ()-getPos().getZ())).normalize();
            if (!getWorld().isRemote){
                double posX = getPos().getX()+0.5;
                double posY = getPos().getY()+0.5;
                double posZ = getPos().getZ()+0.5;
                boolean doContinue = true;
                for (int i = 0; i < 640 && doContinue; i++){
                    posX += ray.x*0.1;
                    posY += ray.y*0.1;
                    posZ += ray.z*0.1;
                    IBlockState state = getWorld().getBlockState(new BlockPos(posX,posY,posZ));
                    TileEntity tile = getWorld().getTileEntity(new BlockPos(posX,posY,posZ));
                    if(sparkTarget(tile))
                        doContinue = false;
                    else if (tile instanceof IEmberPacketReceiver){
                        if (tile.hasCapability(EmberCapabilityProvider.emberCapability, null)){
                            tile.getCapability(EmberCapabilityProvider.emberCapability, null).addAmount(capability.getEmber(), true);
                            tile.markDirty();
                        }
                        doContinue = false;
                    }
                    else if (state.isFullCube() && state.isOpaqueCube()){
                        doContinue = false;
                    }
                    //TODO: OPTIMIZE THIS, THIS CALL IS GARBAGE
                    List<EntityLivingBase> rawEntities = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(posX-0.85,posY-0.85,posZ-0.85,posX+0.85,posY+0.85,posZ+0.85));
                    for (int j = 0; j < rawEntities.size(); j ++){
                        rawEntities.get(j).attackEntityFrom(RegistryManager.damage_ember, 25.0f);
                    }
                }
                this.capability.setEmber(0);
                markDirty();
                PacketHandler.INSTANCE.sendToAll(new MessageBeamCannonFX(this));
                world.playSound(null,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5, SoundEvents.BEAM_CANNON_FIRE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    public boolean sparkTarget(TileEntity target) {
        if (target instanceof TileEntityAlchemyTablet){
            ((TileEntityAlchemyTablet)target).sparkProgress();
            return true;
        }
        if (target instanceof ISparkable) {
            ((ISparkable) target).sparkProgress(this,capability.getEmber());
            return true;
        }
        return false;
    }
}
