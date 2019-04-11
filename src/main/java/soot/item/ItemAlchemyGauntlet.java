package soot.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import soot.Soot;
import soot.network.PacketHandler;
import soot.network.message.MessageAlchemyRingFX;
import soot.network.message.MessageGauntletActivate;
import soot.network.message.MessageGauntletDodge;
import soot.network.message.MessageGauntletRotate;
import soot.particle.ParticleUtilSoot;
import soot.projectiles.ProjectileFireBlast;
import soot.util.Attributes;
import teamroots.embers.api.projectile.EffectDamage;
import teamroots.embers.api.projectile.IProjectilePreset;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemAlchemyGauntlet extends Item {
    static int escapeCooldown;

    public ItemAlchemyGauntlet() {
        this.setMaxStackSize(1);
        this.addPropertyOverride(new ResourceLocation(Soot.MODID,"gauntlet_state"), (stack, worldIn, entityIn) -> getGauntletState(stack, entityIn));
        this.addPropertyOverride(new ResourceLocation(Soot.MODID,"gauntlet_hand"), (stack, worldIn, entityIn) -> getGauntletHand(stack, entityIn));
    }

    public void activateBlock(ItemStack stack, EntityLivingBase player, EnumHand hand, BlockPos pos, EnumFacing facing) {
        ItemStack elixir = getElixir(stack,player);
        if(!elixir.isEmpty()) {
            ItemElixir itemElixir = (ItemElixir) elixir.getItem();
            itemElixir.activateBlock(stack, elixir, player, hand, pos, facing);
        }
    }

    public void activate(ItemStack stack, EntityLivingBase player, EnumHand hand, Vec3d dir) {
        double handmod = player.getActiveHand() == EnumHand.MAIN_HAND ? 1.0 : -1.0;
        double posX = player.posX + player.getLookVec().x + handmod * (player.width / 2.0) * Math.sin(Math.toRadians(-player.rotationYaw - 90));
        double posY = player.posY + player.getEyeHeight() - 0.2 + player.getLookVec().y;
        double posZ = player.posZ + player.getLookVec().z + handmod * (player.width / 2.0) * Math.cos(Math.toRadians(-player.rotationYaw - 90));
        ItemStack elixir = getElixir(stack,player);
        if(!elixir.isEmpty()) {
            ItemElixir itemElixir = (ItemElixir) elixir.getItem();
            itemElixir.activate(stack, elixir, player, hand, dir);
            Vec3d emitPos = new Vec3d(posX,posY,posZ);
            IProjectilePreset projectile = new ProjectileFireBlast(player, emitPos, emitPos.add(player.getLookVec().scale(10)), new EffectDamage(200.0f, x -> DamageSource.DROWN, 0, 0), getElixirColor(elixir), 3, 33, 3.0);
            projectile.shoot(player.world);
        }
    }

    public void dodge(ItemStack stack, EntityLivingBase player, EnumHand hand, Vec3d visualPos) {
        ItemStack elixir = getElixir(stack,player);
        Color mainColor = getElixirColor(elixir);
        PacketHandler.INSTANCE.sendToAll(new MessageAlchemyRingFX(visualPos.x,visualPos.y,visualPos.z,mainColor,30,2.0f,true));
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

    public boolean isComboReset(EntityLivingBase entity) {
        return !(entity instanceof EntityPlayer) || ((EntityPlayer)entity).getCooledAttackStrength(0) >= 1;
    }

    public int getCombo(ItemStack stack, EntityLivingBase entity) {
        NBTTagCompound compound = stack.getTagCompound();
        if(isComboReset(entity) || compound == null)
            return 0;
        return compound.getInteger("combo");
    }

    public void setCombo(ItemStack stack, EntityLivingBase entity, int combo) {
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
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        escapeCooldown--;
        if(entity instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase) entity;
            if (isComboReset(entityLiving))
                setCombo(stack, entityLiving, 0);
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        int combo = getCombo(stack,attacker);
        Attributes.increaseAttraction(target,combo);
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        int combo = getCombo(stack,entityLiving);
        entityLiving.motionX = 0;
        if(entityLiving.onGround)
            entityLiving.motionY = 0;
        else
            entityLiving.motionY = 0.3 / (0.1*combo+1);
        entityLiving.motionZ = 0;
        setCombo(stack,entityLiving,combo+1);
        if(entityLiving.world.isRemote) {
            /*double yaw = (itemRand.nextDouble() - 0.5) * 2.0 * Math.PI;
            double pitch = (itemRand.nextDouble() - 0.5) * 2.0 * Math.PI;
            double yawStart = yaw - Math.toRadians(entityLiving.rotationYaw);
            double pitchStart = pitch - Math.toRadians(entityLiving.rotationPitch);
            double yawEnd = -yaw - Math.toRadians(entityLiving.rotationYaw);
            double pitchEnd = -pitch - Math.toRadians(entityLiving.rotationPitch);
            ParticleUtilSoot.spawnCrystalStrike(entityLiving, 0, entityLiving.getEyeHeight(), 0,
                    t -> new Vec3d(0, 0, 1).rotatePitch((float) MathHelper.clampedLerp(pitchStart, pitchEnd, t)).rotateYaw((float) MathHelper.clampedLerp(yawStart, yawEnd, t)),
                    t -> new Vec3d(MathHelper.clampedLerp(yawStart, yawEnd, t) + (itemRand.nextDouble() - 0.5), MathHelper.clampedLerp(pitchStart, pitchEnd, t) + (itemRand.nextDouble() - 0.5), 0),
                    t -> new Color(255, 64, 16),
                    t -> (float)Math.sin(t*Math.PI) * 0.1f,
                    10);*/

        }
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
            ItemStack elixir = getElixir(stack,player);
            Color mainColor = getElixirColor(elixir);
            Vec3d startPos = player.getPositionEyes(1.0f);
            Vec3d endPos = startPos.add(player.getLookVec().scale(6.0));
            RayTraceResult rayTrace = world.rayTraceBlocks(startPos,endPos,!player.isInWater(),true,false);
            if(rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = rayTrace.getBlockPos();
                IBlockState state = world.getBlockState(pos);
                AxisAlignedBB aabb = state.getSelectedBoundingBox(world,pos);
                if(chargeCoeff - 0.1 > 1.0 && !aabb.hasNaN() && aabb.getAverageEdgeLength() > 0)
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
                }
            }

            if(chargeCoeff > 0.1) {
                double scale = MathHelper.clampedLerp(0.0, 1.0, chargeCoeff - 0.1);
                ParticleUtilSoot.spawnParticleCube(world, posX, posY, posZ, 0, 0, 0, mainColor, (float) (0.5 * scale), 5);
                ParticleUtilSoot.spawnLightning(world, posX, posY, posZ, posX + rx, posY + ry, posZ + rz, 10, 0.1, mainColor, 0.5, 5);
            }
            //ParticleUtilSoot.spawnLightning(world,targetX,targetY,targetZ,targetX+rx,targetY+ry,targetZ+rz,10,0.1, mainColor, 0.5, 5);

        }
    }

    private Color getElixirColor(ItemStack elixir) {
        if(!elixir.isEmpty()) {
            ItemElixir elixirItem = (ItemElixir) elixir.getItem();
            Color color = elixirItem.getColor(elixir);
            return color != null ? color : Color.WHITE;
        } else {
            return Color.WHITE;
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase player, int timeLeft) {
        World world = player.world;
        double chargeCoeff = (getMaxItemUseDuration(stack) - timeLeft) / 20.0;
        if (world.isRemote) {
            double handmod = player.getActiveHand() == EnumHand.MAIN_HAND ? 1.0 : -1.0;
            ItemStack elixir = getElixir(stack,player);
            Color mainColor = getElixirColor(elixir);
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

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3.2, 0));
        }

        return multimap;
    }
}
