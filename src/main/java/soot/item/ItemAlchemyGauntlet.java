package soot.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import soot.Soot;
import soot.network.PacketHandler;
import soot.network.message.*;
import soot.particle.ParticleUtilSoot;
import soot.util.Attributes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemAlchemyGauntlet extends Item {
    private static final double ATTRACTION_STACK = 100;
    static int escapeCooldown;

    public ItemAlchemyGauntlet() {
        this.setMaxStackSize(1);
        this.addPropertyOverride(new ResourceLocation(Soot.MODID,"gauntlet_state"), (stack, worldIn, entityIn) -> getGauntletState(stack, entityIn));
        this.addPropertyOverride(new ResourceLocation(Soot.MODID,"gauntlet_hand"), (stack, worldIn, entityIn) -> getGauntletHand(stack, entityIn));
    }

    public void activateBlock(ItemStack stack, EntityLivingBase player, EnumHand hand, BlockPos pos, EnumFacing facing) {
        activate(stack,player,hand,player.getLookVec());
    }

    public void activate(ItemStack stack, EntityLivingBase player, EnumHand hand, Vec3d dir) {
        World world = player.world;
        AxisAlignedBB aabb = new AxisAlignedBB(player.getPosition());
        aabb = aabb.grow(8,8,8);
        for(Entity entity : world.getEntitiesInAABBexcluding(player, aabb, this::isAttracted)) {
            AxisAlignedBB entityBox = entity.getEntityBoundingBox();
            Vec3d center = entityBox.getCenter();
            Color mainColor = new Color(16, 255, 64);
            PacketHandler.INSTANCE.sendToAllTracking(new MessageAlchemyBlastFX(center.x,center.y,center.z,mainColor,mainColor,mainColor,1.0f,5),entity);
            resetAttraction(entity,1);
        }
    }

    public void dodge(ItemStack stack, EntityLivingBase player, EnumHand hand, Vec3d visualPos) {
        //World world = player.world;
        Color mainColor = new Color(16, 255, 64);
        PacketHandler.INSTANCE.sendToAllTracking(new MessageAlchemyRingFX(visualPos.x,visualPos.y,visualPos.z,mainColor,30,2.0f,true),player);
    }

    public boolean isAttracted(Entity entity) {
        if(entity instanceof EntityLivingBase) {
            IAttributeInstance attraction = ((EntityLivingBase) entity).getEntityAttribute(Attributes.ATTRACTION);
            return attraction.getAttributeValue() >= 0;
        }
        return false;
    }

    public void increaseAttraction(Entity entity, double amount) {
        if(entity instanceof EntityLivingBase) {
            IAttributeInstance attraction = ((EntityLivingBase) entity).getEntityAttribute(Attributes.ATTRACTION);
            attraction.setBaseValue(attraction.getBaseValue()+amount);
        }
    }

    public void resetAttraction(Entity entity, int stacks) {
        if(entity instanceof EntityLivingBase) {
            IAttributeInstance attraction = ((EntityLivingBase) entity).getEntityAttribute(Attributes.ATTRACTION);
            double amount = attraction.getBaseValue();
            amount -= amount % ATTRACTION_STACK; //Take away the buildup to the next stack
            amount -= ATTRACTION_STACK; //And the stack itself
            attraction.setBaseValue(amount);
        }
    }

    public void rotate(ItemStack stack, EntityLivingBase player, EnumHand hand) {
        NBTTagCompound compound = getOrCreateTagCompound(stack);
        compound.setInteger("rotation",compound.getInteger("rotation")+1);
    }

    public int getRotation(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null)
            return 0;
        return compound.getInteger("rotation");
    }

    public ItemStack getElixir(ItemStack stack, EntityLivingBase entity) {
        List<ItemStack> elixirs = getElixirs(entity);
        int rotation = getRotation(stack);
        if(elixirs.isEmpty())
            return ItemStack.EMPTY;
        return elixirs.get(rotation % elixirs.size());
    }

    public static List<ItemStack> getElixirs(EntityLivingBase entity) {
        List<ItemStack> elixirs = new ArrayList<>();
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            InventoryPlayer inventory = player.inventory;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack item = inventory.getStackInSlot(i);
                if (item.getItem() instanceof ItemElixir)
                    elixirs.add(item);
            }
        }
        return elixirs;
    }

    public int getCombo(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null)
            return 0;
        return compound.getInteger("combo");
    }

    public void setCombo(ItemStack stack, int combo) {
        NBTTagCompound compound = getOrCreateTagCompound(stack);
        compound.setInteger("combo", combo);
    }

    public NBTTagCompound getOrCreateTagCompound(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }
        return compound;
    }

    public float getGauntletState(ItemStack stack, EntityLivingBase entity) {
        if(entity != null && entity.getActiveItemStack() == stack)
            return 1;
        return 0;
    }

    public float getGauntletHand(ItemStack stack, EntityLivingBase entity) {
        if(entity != null) {
            if(entity.getHeldItemMainhand() == stack)
                return entity.getPrimaryHand() == EnumHandSide.RIGHT ? 0 : 1;
            else
                return entity.getPrimaryHand() == EnumHandSide.LEFT ? 0 : 1;
        }
        return 1;
    }

    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if(escapeCooldown > 0)
            return new ActionResult<>(EnumActionResult.FAIL, itemstack);
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        escapeCooldown--;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        increaseAttraction(target,getCombo(stack));
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        int combo = getCombo(stack);
        entityLiving.motionX = 0;
        if(entityLiving.onGround)
            entityLiving.motionY = 0;
        else
            entityLiving.motionY = 0.3 / (0.1*combo+1);
        entityLiving.motionZ = 0;
        setCombo(stack,combo+1);
        return super.onEntitySwing(entityLiving, stack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
        World world = player.world;
        if (world.isRemote) {
            double handmod = player.getActiveHand() == EnumHand.MAIN_HAND ? 1.0 : -1.0;
            double posX = player.posX + player.getLookVec().x + handmod * (player.width / 2.0) * Math.sin(Math.toRadians(-player.rotationYaw - 90));
            double posY = player.posY + player.getEyeHeight() - 0.2 + player.getLookVec().y;
            double posZ = player.posZ + player.getLookVec().z + handmod * (player.width / 2.0) * Math.cos(Math.toRadians(-player.rotationYaw - 90));
            //double dx = player.getLookVec().x;
            //double dy = player.getLookVec().y;
            //double dz = player.getLookVec().z;
            double chargeCoeff = (getMaxItemUseDuration(stack) - timeLeft) / 20.0;
            //double dist = chargeCoeff * 1.0;
            //double targetX = posX + dx * dist;
            //double targetY = posY + dy * dist;
            //double targetZ = posZ + dz * dist;
            double lightningRadius = 0.3;
            double rx = (itemRand.nextDouble() - 0.5) * 2.0 * lightningRadius;
            double ry = (itemRand.nextDouble() - 0.5) * 2.0 * lightningRadius;
            double rz = (itemRand.nextDouble() - 0.5) * 2.0 * lightningRadius;
            Color mainColor = new Color(16, 255, 64);
            Vec3d startPos = player.getPositionEyes(1.0f);
            Vec3d endPos = startPos.add(player.getLookVec().scale(6.0));
            RayTraceResult rayTrace = world.rayTraceBlocks(startPos,endPos,!player.isInWater(),true,false);
            if(rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = rayTrace.getBlockPos();
                IBlockState state = world.getBlockState(pos);
                AxisAlignedBB aabb = state.getSelectedBoundingBox(world,pos);
                if(chargeCoeff-0.1 > 1.0 && aabb != null && !aabb.hasNaN() && aabb.getAverageEdgeLength() > 0)
                for (int i = 0; i < 12; ++i)
                {
                    int ax = (i >> 2) & 3;
                    int as = (4-ax) >> 2;
                    int bs = (5-ax) >> 2;
                    int v1 = ((i&1) << as) | ((i&2) << bs);
                    int v2 = v1|(1<<ax);
                    int dx1 = v1 & 1;
                    int dy1 = (v1>>1) & 1;
                    int dz1 = (v1>>2) & 1;
                    int dx2 = v2 & 1;
                    int dy2 = (v2>>1) & 1;
                    int dz2 = (v2>>2) & 1;
                    ParticleUtilSoot.spawnLightning(world,
                            dx1 == 0 ? aabb.minX : aabb.maxX,
                            dy1 == 0 ? aabb.minY : aabb.maxY,
                            dz1 == 0 ? aabb.minZ : aabb.maxZ,
                            dx2 == 0 ? aabb.minX : aabb.maxX,
                            dy2 == 0 ? aabb.minY : aabb.maxY,
                            dz2 == 0 ? aabb.minZ : aabb.maxZ, 5, 0.1, mainColor, 0.5, 2);
                    //System.out.println(String.format("start: %d,%d,%d",dx1,dy1,dz1));
                    //System.out.println(String.format("end: %d,%d,%d",dx2,dy2,dz2));
                }
                /*ParticleUtilSoot.spawnLightning(world, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, 5, 0.1, mainColor, 0.5, 2);
                ParticleUtilSoot.spawnLightning(world, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, 5, 0.1, mainColor, 0.5, 2);
                ParticleUtilSoot.spawnLightning(world, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, 5, 0.1, mainColor, 0.5, 2);
                ParticleUtilSoot.spawnLightning(world, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, 5, 0.1, mainColor, 0.5, 2);
                ParticleUtilSoot.spawnLightning(world, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, 5, 0.1, mainColor, 0.5, 2);
                ParticleUtilSoot.spawnLightning(world, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, 5, 0.1, mainColor, 0.5, 2);
                ParticleUtilSoot.spawnLightning(world, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, 5, 0.1, mainColor, 0.5, 2);
                ParticleUtilSoot.spawnLightning(world, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, 5, 0.1, mainColor, 0.5, 2);*/
            }

            if(chargeCoeff > 0.1) {
                double scale = MathHelper.clampedLerp(0.0, 1.0, chargeCoeff - 0.1);
                ParticleUtilSoot.spawnParticleCube(world, posX, posY, posZ, 0, 0, 0, mainColor, (float) (0.5 * scale), 5);
                ParticleUtilSoot.spawnLightning(world, posX, posY, posZ, posX + rx, posY + ry, posZ + rz, 10, 0.1, mainColor, 0.5, 5);
            }
            //ParticleUtilSoot.spawnLightning(world,targetX,targetY,targetZ,targetX+rx,targetY+ry,targetZ+rz,10,0.1, mainColor, 0.5, 5);

        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase player, int timeLeft) {
        World world = player.world;
        double chargeCoeff = (getMaxItemUseDuration(stack) - timeLeft) / 20.0;
        if (world.isRemote) {
            double handmod = player.getActiveHand() == EnumHand.MAIN_HAND ? 1.0 : -1.0;
            Color mainColor = new Color(16, 255, 64);
            double posX = player.posX + player.getLookVec().x + handmod * (player.width / 2.0) * Math.sin(Math.toRadians(-player.rotationYaw - 90));
            double posY = player.posY + player.getEyeHeight() - 0.2 + player.getLookVec().y;
            double posZ = player.posZ + player.getLookVec().z + handmod * (player.width / 2.0) * Math.cos(Math.toRadians(-player.rotationYaw - 90));
            if (chargeCoeff > 0.1) {
                if(chargeCoeff-0.1 > 1.0) {
                    player.motionX = 0;
                    if (player.onGround)
                        player.motionY = 0;
                    else
                        player.motionY = 0.2;
                    player.motionZ = 0;
                    ParticleUtilSoot.spawnCubeRing(world, posX, posY, posZ, mainColor, 30, 1);
                    RayTraceResult rayTrace = getTrace(player, world, posX, posY, posZ);
                    if(rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
                        BlockPos pos = rayTrace.getBlockPos();
                        EnumFacing side = rayTrace.sideHit;
                        PacketHandler.INSTANCE.sendToServer(new MessageGauntletActivate(player.getActiveHand(),pos,side,rayTrace.hitVec.x,rayTrace.hitVec.y,rayTrace.hitVec.z));
                    } else {
                        Vec3d look = player.getLookVec();
                        PacketHandler.INSTANCE.sendToServer(new MessageGauntletActivate(player.getActiveHand(),look.x,look.y,look.z));
                    }
                    //PacketHandler.INSTANCE.sendToServer(new MessageGauntletActivate());
                }
            } else if (Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()) {
                if(escapeCooldown <= 0) {
                    escapeCooldown = 40;
                    Vec3d forward = player.getForward();
                    player.motionX = -forward.x;
                    if (player.onGround)
                        player.motionY = 0.1;
                    else
                        player.motionY = 0;
                    player.motionZ = -forward.z;
                    PacketHandler.INSTANCE.sendToServer(new MessageGauntletDodge(player.getActiveHand(), new Vec3d(posX, posY, posZ)));
                    //ParticleUtilSoot.spawnCubeRing(world, posX, posY, posZ, mainColor, 30, 2);
                    //ParticleUtilSoot.spawnParticleCube(world, posX, posY, posZ, 0, 0, 0, mainColor, 2.0f, 10);
                }
            } else {
                PacketHandler.INSTANCE.sendToServer(new MessageGauntletRotate(player.getActiveHand()));
            }
            /*double dx = player.getLookVec().x;
            double dy = player.getLookVec().y;
            double dz = player.getLookVec().z;
            double dist = (getMaxItemUseDuration(stack)-timeLeft) / 10.0;
            double targetX = posX + dx * dist;
            double targetY = posY + dy * dist;
            double targetZ = posZ + dz * dist;

            Color mainColor = new Color(16, 255, 64);
            Color secondColor = new Color(255, 64, 16);
            ParticleUtilSoot.spawnLightning(world,posX,posY,posZ,targetX,targetY,targetZ,10,0.5, mainColor, 2, 20);

            ParticleUtilSoot.spawnAlchemyExplosion(world,targetX,targetY,targetZ, mainColor,new Color(0,0,128),secondColor,1f,20);*/
        }
    }

    private RayTraceResult getTrace(EntityLivingBase player, World world, double posX, double posY, double posZ) {
        Vec3d startPos = new Vec3d(posX,posY,posZ);
        Vec3d endPos = startPos.add(player.getLookVec().scale(2.0));
        return world.rayTraceBlocks(startPos,endPos,!player.isInWater(),true,false);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
