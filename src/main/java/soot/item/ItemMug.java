package soot.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.brewing.CaskManager;
import soot.brewing.CaskManager.CaskLiquid;
import soot.util.IItemColored;
import soot.util.MiscUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMug extends ItemFluidContainer implements IItemColored {

    public static final int MAX_STACK_SIZE = 8;
    public static final int CAPACITY = 250;

    public ItemMug()
    {
        super(CAPACITY);
        this.setHasSubtypes(true);
        this.setMaxStackSize(MAX_STACK_SIZE);
        this.addPropertyOverride(new ResourceLocation("fillmodel"), (stack, worldIn, entityIn) -> {
            CaskLiquid liquid = getCaskLiquid(stack);
            if (liquid != null) return liquid.model;

            return 0;
        });
    }

    public CaskLiquid getCaskLiquid(ItemStack stack) {
        return CaskManager.getFromFluid(FluidUtil.getFluidContained(stack));
    }

    public ItemStack getEmpty()
    {
        return new ItemStack(this);
    }

    public ItemStack getFilled(CaskLiquid liquid)
    {
        ItemStack filled = new ItemStack(this);
        IFluidHandlerItem tank = filled.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null);
        if(tank != null)
            tank.fill(new FluidStack(liquid.fluid,capacity),true);
        return filled;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab))
        {
            items.add(getEmpty());
            items.addAll(CaskManager.liquids.stream().map(this::getFilled).collect(Collectors.toList()));
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;

        if (!worldIn.isRemote)
        {
            FluidStack fluid = FluidUtil.getFluidContained(stack);
            CaskLiquid liquid = CaskManager.getFromFluid(fluid);

            if(liquid != null)
                liquid.applyEffects(entityLiving, entityplayer, entityplayer, fluid);
        }

        if (entityplayer instanceof EntityPlayerMP)
        {
            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
        }

        if (entityplayer != null)
        {
            entityplayer.addStat(StatList.getObjectUseStats(this));
        }

        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode)
        {
            stack.shrink(1);

            if (stack.isEmpty())
            {
                return getEmpty();
            }

            if (entityplayer != null)
            {
                entityplayer.inventory.addItemStackToInventory(getEmpty());
            }
        }

        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        FluidStack fluid = FluidUtil.getFluidContained(stack);
        return (int)((soot.util.FluidUtil.getModifier(fluid,"viscosity") / 1000f) * 35f);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(FluidUtil.getFluidContained(stack) == null)
            return new ActionResult<>(EnumActionResult.PASS, stack);
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        FluidStack fluid = FluidUtil.getFluidContained(stack);
        if(fluid != null)
            return I18n.translateToLocalFormatted("item.mug.name",fluid.getLocalizedName());
        else
            return I18n.translateToLocal("item.mug.empty.name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        FluidStack fluid = FluidUtil.getFluidContained(stack);
        if(fluid != null) {
            MiscUtil.addPotionEffectTooltip(soot.util.FluidUtil.getPrimaryEffects(fluid), tooltip, 1.0F);
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidHandlerItemStack(stack,capacity)
        {
            @Override
            public int fill(FluidStack resource, boolean doFill) {
                if(resource == null || resource.amount < capacity)
                    return 0;

                return super.fill(resource, doFill);
            }
        };
    }

    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex) {
        if(tintIndex == 1)
        {
            CaskLiquid liquid = getCaskLiquid(stack);
            if(liquid != null)
                return liquid.color;
        }

        return 0xFFFFFFFF;
    }
}
