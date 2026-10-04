package mob_grinding_utils.inventory.server;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public record InventoryWrapperAH(Container inv) implements IItemHandlerModifiable {

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        InventoryWrapperAH that = (InventoryWrapperAH) o;

        return inv().equals(that.inv());

    }

    @Override
    public int hashCode() {
        return inv().hashCode();
    }

    @Override
    public int getSlots() {
        return inv().getContainerSize();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return inv().getItem(slot);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack stackInSlot = inv().getItem(slot);

        int m;
        if (!stackInSlot.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(stack, stackInSlot)) //TODO maybe? was ItemHandlerHelper.canItemStacksStack
                return stack;

            if (!inv().canPlaceItem(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

            if (stack.getCount() <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    inv().setItem(slot, copy);
                    inv().setChanged();
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    inv().setItem(slot, copy);
                    inv().setChanged();
                } else {
                    stack.shrink(m);
                }
                return stack;
            }
        } else {
            if (!inv().canPlaceItem(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount()) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    inv().setItem(slot, stack.split(m));
                    inv().setChanged();
                } else {
                    stack.shrink(m);
                }
                return stack;
            } else {
                if (!simulate) {
                    inv().setItem(slot, stack);
                    inv().setChanged();
                }
                return ItemStack.EMPTY;
            }
        }

    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack stackInSlot = inv().getItem(slot);

        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (slot == 0)
            return ItemStack.EMPTY;

        if (simulate) {
            if (stackInSlot.getCount() < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.getCount(), amount);

            ItemStack decrStackSize = inv().removeItem(slot, m);
            inv().setChanged();
            return decrStackSize;
        }
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        inv().setItem(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return inv().getMaxStackSize();
    }


    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return slot != 0;
    }
}