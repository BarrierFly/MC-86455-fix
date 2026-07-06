package cc.akio.mc86455fix.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes MC-86455: Creative-mode pick block causes client–server inventory
 * desync when the target item resides in the main inventory (slots 9–35).
 *
 * <h3>Root cause</h3>
 * <p>In creative mode, {@code Minecraft.pickBlock()} calls
 * {@code Inventory.setPickedItem()} which may call {@code pickSlot()}
 * to <em>swap</em> the hotbar item with an inventory item.  Only the
 * hotbar slot change is sent to the server via
 * {@code handleCreativeModeItemAdd()}.  The inventory slot change
 * never reaches the server, causing item duplication/loss.</p>
 *
 * <h3>Fix</h3>
 * <p>This {@code @Redirect} intercepts the {@code setPickedItem} call,
 * records which slots are affected, and sends the missing sync packets.
 * Works across Minecraft 1.17.1 – 1.20.6.</p>
 */
@Mixin(Minecraft.class)
public abstract class PickBlockMixin {

    /**
     * Intercepts {@code Inventory.setPickedItem(ItemStack)} inside
     * {@code Minecraft.pickBlock()}.  The vanilla call performs the
     * inventory manipulation on the client; we add the server sync
     * for every slot that was changed.
     *
     * @param inventory the player's inventory
     * @param stack     the ItemStack being picked
     */
    @Redirect(
        method = "pickBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;setPickedItem(Lnet/minecraft/world/item/ItemStack;)V"
        )
    )
    private void redirectSetPickedItem(Inventory inventory, ItemStack stack) {
        // ---- snapshot before the operation ----
        int foundSlot = inventory.findSlotMatchingItem(stack);
        int oldSelected = inventory.selected;
        ItemStack oldItem = inventory.getItem(oldSelected).copy();

        // ---- let vanilla do its thing ----
        inventory.setPickedItem(stack);

        Minecraft self = (Minecraft) (Object) this;
        MultiPlayerGameMode gameMode = self.gameMode;
        if (gameMode == null) return;

        // ----------------------------------------------------------
        // Case A: item was in main inventory (slots 9-35)
        //   → pickSlot() SWAPPED selected ↔ foundSlot
        //   → foundSlot now holds the OLD hotbar item
        //   Vanilla will sync the hotbar slot; we must sync foundSlot.
        // ----------------------------------------------------------
        if (foundSlot != -1 && !Inventory.isHotbarSlot(foundSlot)) {
            ItemStack displaced = inventory.getItem(foundSlot);
            if (!displaced.isEmpty() && !ItemStack.matches(displaced, stack)) {
                gameMode.handleCreativeModeItemAdd(displaced, foundSlot);
            }
            return;
        }

        // ----------------------------------------------------------
        // Case B: item was NOT in inventory
        //   → setPickedItem chose a suitable hotbar slot, moved the
        //     old item (if any) to a free slot, and placed `stack`.
        //   Vanilla syncs the hotbar slot; we also sync the free slot.
        // ----------------------------------------------------------
        if (foundSlot == -1 && !oldItem.isEmpty()) {
            int newSel = inventory.selected;
            for (int slot = 0; slot < 36; slot++) {
                if (slot == newSel) continue;
                ItemStack candidate = inventory.getItem(slot);
                if (ItemStack.matches(oldItem, candidate)) {
                    gameMode.handleCreativeModeItemAdd(candidate, slot);
                    break;
                }
            }
        }
        // Case C: item already in hotbar → no slot changes, nothing to do
    }
}
