package de.siphalor.mousewheelie.client.mixin;

import de.siphalor.mousewheelie.client.Config;
import de.siphalor.mousewheelie.client.util.ISlot;
import de.siphalor.mousewheelie.client.util.inventory.SlotRefiller;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.Container;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Container.class)
public abstract class MixinContainer {
	@Shadow public abstract Slot getSlot(int int_1);

	private static boolean mouseWheelie_scheduleRefill = false;

	@Inject(method = "updateSlotStacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/container/Container;getSlot(I)Lnet/minecraft/container/Slot;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void onSlotUpdate(List<ItemStack> itemStacks, CallbackInfo callbackInfo, int index) {
		if((Object) this instanceof PlayerContainer && Config.otherRefill.value) {
			PlayerInventory inventory = MinecraftClient.getInstance().player.inventory;
			if(inventory.selectedSlot == ((ISlot) getSlot(index)).mouseWheelie_getInvSlot()) {
				ItemStack stack = inventory.getMainHandStack();
				if (!stack.isEmpty() && itemStacks.get(index).isEmpty()) {
					mouseWheelie_scheduleRefill = true;
					SlotRefiller.set(inventory, stack.copy());
				}
			}
		}
	}

	@Inject(method = "updateSlotStacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/container/Slot;setStack(Lnet/minecraft/item/ItemStack;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void onSlotUpdated(List<ItemStack> stacks, CallbackInfo callbackInfo, int index) {
		if(mouseWheelie_scheduleRefill) {
			mouseWheelie_scheduleRefill = false;
			SlotRefiller.refill();
		}
	}

}
