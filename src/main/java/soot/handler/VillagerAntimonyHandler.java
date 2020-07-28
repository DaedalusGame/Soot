package soot.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.SlotMerchantResult;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import soot.Registry;

import javax.annotation.Nullable;
import java.awt.event.ContainerEvent;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class VillagerAntimonyHandler {
    private static Field buyingList;
    private static Field containerMerchant;

    private static HashMap<ContainerMerchant,ContainerWrapper> openContainers = new HashMap<>();
    private static ContainerWrapper clientContainer;

    private static class ContainerWrapper
    {
        ContainerMerchant container;
        MerchantRecipeList defaultList;
        MerchantRecipeList replacedList;
        IMerchant merchant;
        boolean dirty;

        public ContainerWrapper(ContainerMerchant container)
        {
            this.container = container;
            this.merchant = getContainerMerchant(container);
        }

        public void update()
        {
            if(merchant.getCustomer() == null)
                return;
            InventoryMerchant inventory = container.getMerchantInventory();
            ItemStack firstStack = inventory.getStackInSlot(0);
            ItemStack secondStack = inventory.getStackInSlot(1);
            MerchantRecipeList currentList = merchant.getRecipes(merchant.getCustomer());
            if(currentList != replacedList && currentList != defaultList) {
                defaultList = currentList; //We have a new default list, mark dirty so we generate a new replacement
                dirty = true;
            }
            if(firstStack.getItem() == Registry.SIGNET_ANTIMONY || secondStack.getItem() == Registry.SIGNET_ANTIMONY)
            {
                if(dirty) {
                    replacedList = generateReplacement(defaultList);
                    dirty = false;
                }
                if(currentList != replacedList) {
                    replaceList(merchant, replacedList);
                    container.getMerchantInventory().resetRecipeAndSlots();
                }
            }
            else
            {
                if(currentList != defaultList) {
                    replaceList(merchant, defaultList);
                    container.getMerchantInventory().resetRecipeAndSlots();
                }
            }
        }

        public void resetList()
        {
            if(defaultList != null)
                replaceList(merchant, defaultList);
        }

        public MerchantRecipeList generateReplacement(MerchantRecipeList original)
        {
            return original.stream().map(VillagerAntimonyHandler::getAntimonyEquivalent).collect(Collectors.toCollection(MerchantRecipeList::new));
        }

        public void replaceList(IMerchant merchant, MerchantRecipeList list)
        {
            if(merchant instanceof EntityVillager)
            {
                replaceTradeList((EntityVillager) merchant,list);
            }
            else if(merchant.getWorld().isRemote)
            {
                merchant.setRecipes(list);
            }
        }
    }

    /*public static class WrappedSlotMerchantResult extends SlotMerchantResult {
        private InventoryMerchant merchantInventory;
        private IMerchant merchant;

        public WrappedSlotMerchantResult(EntityPlayer player, IMerchant merchant, InventoryMerchant inventory, SlotMerchantResult oldSlot) {
            super(player, merchant, inventory, oldSlot.getSlotIndex(), oldSlot.xPos, oldSlot.yPos);
            this.slotNumber = oldSlot.slotNumber;
            this.merchant = merchant;
            this.merchantInventory = inventory;
        }

        @Override
        public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
            this.onCrafting(stack);
            MerchantRecipe merchantrecipe = this.merchantInventory.getCurrentRecipe();

            if (merchantrecipe != null) {
                ItemStack firstStackReal = this.merchantInventory.getStackInSlot(0);
                ItemStack secondStackReal = this.merchantInventory.getStackInSlot(1);
                ItemStack firstStack = getCurrencyStack(firstStackReal);
                ItemStack secondStack = getCurrencyStack(secondStackReal);

                if (this.doTrade(merchantrecipe, firstStack, secondStack) || this.doTrade(merchantrecipe, secondStack, firstStack)) {
                    this.merchant.useRecipe(merchantrecipe);
                    thePlayer.addStat(StatList.TRADED_WITH_VILLAGER);
                    firstStackReal.setCount(firstStack.getCount());
                    secondStackReal.setCount(secondStack.getCount());
                    this.merchantInventory.setInventorySlotContents(0, firstStackReal);
                    this.merchantInventory.setInventorySlotContents(1, secondStackReal);
                }
            }

            return stack;
        }

        private boolean doTrade(MerchantRecipe trade, ItemStack firstItem, ItemStack secondItem) {
            ItemStack itemstack = trade.getItemToBuy();
            ItemStack itemstack1 = trade.getSecondItemToBuy();

            if (firstItem.getItem() == itemstack.getItem() && firstItem.getCount() >= itemstack.getCount()) {
                if (!itemstack1.isEmpty() && !secondItem.isEmpty() && itemstack1.getItem() == secondItem.getItem() && secondItem.getCount() >= itemstack1.getCount()) {
                    firstItem.shrink(itemstack.getCount());
                    secondItem.shrink(itemstack1.getCount());
                    return true;
                }

                if (itemstack1.isEmpty() && secondItem.isEmpty()) {
                    firstItem.shrink(itemstack.getCount());
                    return true;
                }
            }

            return false;
        }
    }*/

    public static ItemStack getCurrencyStack(ItemStack stack) {
        return stack.getItem() == Registry.SIGNET_ANTIMONY ? new ItemStack(Items.EMERALD, stack.getCount()) : stack;
    }

    public static IMerchant getContainerMerchant(ContainerMerchant container) {
        if (containerMerchant == null)
            containerMerchant = ObfuscationReflectionHelper.findField(ContainerMerchant.class, "field_75178_e");
        IMerchant merchant = null;
        try {
            merchant = (IMerchant) containerMerchant.get(container);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return merchant;
    }

    public static void replaceTradeList(EntityVillager villager, MerchantRecipeList newlist) {
        if (buyingList == null)
            buyingList = ObfuscationReflectionHelper.findField(EntityVillager.class, "field_70963_i");

        if (villager != null) {
            try {
                buyingList.set(villager, newlist);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /*public static void replaceTradeList(EntityVillager villager)
    {
        MerchantRecipeList recipeList = villager.getRecipes(null); //Returns the same stuff irregardless of the player parameter
        if(recipeList != null) {
            ArrayList<MerchantRecipe> recipeListAntimony = new ArrayList<>();

            for(MerchantRecipe recipe : recipeList)
                if(!hasAntimony(recipe))
                {
                    MerchantRecipe recipeAntimony = getAntimonyEquivalent(recipe);
                    if(recipeAntimony != null)
                        recipeListAntimony.add(recipeAntimony);
                }

            recipeList.addAll(recipeListAntimony);
        }
    }*/

    /*private static MerchantRecipeList WrapTradeList(MerchantRecipeList list) {
        if (list != null && !(list instanceof WrappedMerchantRecipeList)) {
            WrappedMerchantRecipeList wrappedList = new WrappedMerchantRecipeList(); //Life is pain
            wrappedList.addAll(list);
            return wrappedList;
        }
        else
            return list;
    }*/

    private static boolean hasAntimony(MerchantRecipe recipe)
    {
        return recipe.getItemToBuy().getItem() == Registry.SIGNET_ANTIMONY || recipe.getSecondItemToBuy().getItem() == Registry.SIGNET_ANTIMONY;
    }

    @Nullable
    private static MerchantRecipe getAntimonyEquivalent(MerchantRecipe recipe)
    {
        ItemStack firstItem = recipe.getItemToBuy();
        ItemStack secondItem = recipe.getSecondItemToBuy();
        if(firstItem.getItem() == Items.EMERALD || secondItem.getItem() == Items.EMERALD) {
            if (firstItem.getItem() == Items.EMERALD)
                firstItem = new ItemStack(Registry.SIGNET_ANTIMONY, firstItem.getCount());
            if (secondItem.getItem() == Items.EMERALD)
                secondItem = new ItemStack(Registry.SIGNET_ANTIMONY, secondItem.getCount());
            return new MerchantRecipe(firstItem,secondItem,recipe.getItemToSell(),recipe.getToolUses(),recipe.getMaxTradeUses());
        }
        else
            return recipe;
    }

    /*@SubscribeEvent
    public static void onUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityVillager)
            replaceTradeList((EntityVillager) entity);
    }*/

    /*@SubscribeEvent
    public static void onGUIOpen(GuiOpenEvent event) {
        GuiScreen gui = event.getGui();
        if (gui instanceof GuiMerchant) {
            IMerchant merchant = ((GuiMerchant) gui).getMerchant();
            ContainerMerchant container = (ContainerMerchant) ((GuiMerchant) gui).inventorySlots;
            if (container != null) {
                EntityPlayer customer = merchant.getCustomer();
                container.inventorySlots.replaceAll(slot -> {
                    if (slot instanceof SlotMerchantResult) {
                        SlotMerchantResult oldSlot = (SlotMerchantResult) slot;
                        return new WrappedSlotMerchantResult(customer, merchant, container.getMerchantInventory(), oldSlot);
                    } else
                        return slot;
                });
            }
        }
    }*/

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        Container container = event.getContainer();

        if(container instanceof ContainerMerchant)
        {
            if(!openContainers.containsKey(container))
                openContainers.put((ContainerMerchant) container, new ContainerWrapper((ContainerMerchant) container));
        }
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event)
    {
        Container container = event.getContainer();

        if(container instanceof ContainerMerchant)
        {
            if(openContainers.containsKey(container)) {
                openContainers.get(container).resetList();
                openContainers.remove(container);
            }
        }
    }

    @SubscribeEvent
    public static void onUpdate(TickEvent.WorldTickEvent event)
    {
        if(event.side == Side.SERVER && event.phase == TickEvent.Phase.END)
        {
            openContainers.values().forEach(ContainerWrapper::update);
        }
    }

    @SubscribeEvent
    public static void onClientUpdate(TickEvent.ClientTickEvent event)
    {
        if(event.side == Side.CLIENT && event.phase == TickEvent.Phase.END)
        {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (gui instanceof GuiMerchant) {
                //IMerchant merchant = ((GuiMerchant) gui).getMerchant();
                ContainerMerchant container = (ContainerMerchant) ((GuiMerchant) gui).inventorySlots;
                if (container != null && (clientContainer == null || clientContainer.container != container)) {
                    clientContainer = new ContainerWrapper(container);
                }
                clientContainer.update();
            }
            else
            {
                clientContainer = null;
            }
        }
    }
}
