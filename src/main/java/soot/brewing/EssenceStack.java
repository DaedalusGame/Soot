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
        if(isEmpty())
            return EssenceType.NULL;
        return essence;
    }

    public int getAmount() {
        if(isEmpty())
            return 0;
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void shrink(int amount) {
        this.amount = Math.max(this.amount - amount, 0);
    }

    public void grow(int amount) {
        this.amount += amount;
    }

    /**
     *
     * @param other
     * @param max how large this stack can get at max
     * @return the remainder of the other stack
     */
    public EssenceStack merge(EssenceStack other, int max) {
        int amount = Math.min(other.amount, max - this.amount);
        grow(amount);
        EssenceStack copy = other.copy();
        copy.shrink(amount);
        return copy;
    }

    /**
     *
     * @param amount
     * @return the newly split off stack
     */
    public EssenceStack split(int amount) {
        amount = Math.min(amount, this.amount);
        EssenceStack copy = withSize(amount);
        shrink(amount);
        return copy;
    }

    public boolean isEmpty() {
        return essence == null || essence == EssenceType.NULL || amount <= 0;
    }

    private void readFromNBT(NBTTagCompound nbt) {
        essence = EssenceType.getType(nbt.getString("type"));
        amount = nbt.getInteger("amount");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setString("type",getEssence().getName());
        nbt.setInteger("amount",getAmount());
        return nbt;
    }

    public EssenceStack copy() {
        return new EssenceStack(essence, amount);
    }

    public EssenceStack withSize(int amount) {
        EssenceStack copy = copy();
        copy.setAmount(amount);
        return copy;
    }
}
