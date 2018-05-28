package soot.tile.overrides;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import soot.SoundEvents;
import soot.capability.IUpgradeProvider;
import soot.util.ISparkable;
import soot.util.UpgradeUtil;
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
    public List<IUpgradeProvider> upgrades;

    @Override
    public void update() {
        IBlockState cannonstate = getWorld().getBlockState(getPos());
        EnumFacing facing = cannonstate.getValue(BlockBeamCannon.facing);
        if (this.target == null && this.ticksExisted == 0){
            this.target = getPos().offset(facing);
        }
        upgrades = UpgradeUtil.getUpgrades(world, pos, new EnumFacing[] {facing.getOpposite()}); //TODO: Cache both of these calls
        UpgradeUtil.verifyUpgrades(this, upgrades);
        ticksExisted++;
        double cost_multiplier = UpgradeUtil.getTotalEmberConsumption(this, upgrades);
        boolean isPowered = getWorld().isBlockIndirectlyGettingPowered(getPos()) != 0;
        //So lets explain why we want to modify the redstone signal value: An upgrade attachment for this block could be
        //a turret base that provides a turret upgrade that auto-aims and auto-fires the cannon, in that case we
        //do not want the player to constantly fire the cannon.
        if (this.capability.getEmber() >= 400 * cost_multiplier && UpgradeUtil.getOtherParameter(this,"redstone_enabled",isPowered,upgrades)){
            fire();
        }
    }

    public void fire() {
        Vec3d ray = (new Vec3d(target.getX()-getPos().getX(),target.getY()-getPos().getY(),target.getZ()-getPos().getZ())).normalize();
        if (!getWorld().isRemote){
            float damage = UpgradeUtil.getOtherParameter(this,"damage",(Float)25.0f,upgrades);
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
                    rawEntities.get(j).attackEntityFrom(RegistryManager.damage_ember, damage);
                }
                if(!doContinue)
                    world.playSound(null,posX,posY,posZ,SoundEvents.BEAM_CANNON_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            this.capability.setEmber(0);
            markDirty();
            PacketHandler.INSTANCE.sendToAll(new MessageBeamCannonFX(this));
            world.playSound(null,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5, SoundEvents.BEAM_CANNON_FIRE, SoundCategory.BLOCKS, 1.0f, 1.0f);
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
