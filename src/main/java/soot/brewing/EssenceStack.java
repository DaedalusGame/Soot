package soot.brewing;

import net.minecraft.nbt.NBTTagCompound;

public class EssenceStack {
    public static final EssenceStack EMPTY = new EssenceStack(null,0);

    private EssenceType essence;
    private int amount;

    public EssenceStack(EssenceType essence, int amount) {
        this.essence = essence;
        this.amount = amount;
    }

    public EssenceStack(NBTTagCompound compound) {
        readFromNBT(compound);
    }

    public EssenceType getEssence() {
        return essence;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void add(EssenceStack other) {
        amount += other.amount;
    }

    public void remove(EssenceStack other) {
        amount = Math.max(0,amount-other.amount);
    }

    public boolean isEmpty() {
        return essence == null || amount <= 0;
    }

    private void readFromNBT(NBTTagCompound nbt) {
        essence = EssenceType.getType(nbt.getString("type"));
        amount = nbt.getInteger("amount");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setString("type",essence.getName());
        nbt.setInteger("amount",amount);
        return nbt;
    }
}
