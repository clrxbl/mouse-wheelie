package de.siphalor.mousewheelie.client.mixin;

import com.mojang.authlib.GameProfile;
import de.siphalor.mousewheelie.client.InteractionManager;
import de.siphalor.mousewheelie.client.util.inventory.SlotRefiller;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
	public MixinClientPlayerEntity(ClientWorld clientWorld_1, GameProfile gameProfile_1) {
		super(clientWorld_1, gameProfile_1);
	}

	@Inject(method = "closeScreen", at = @At("HEAD"))
	public void onScreenClosed(CallbackInfo callbackInfo) {
        InteractionManager.stopSending();
	}

	@Inject(method = "dropSelectedItem", at = @At("HEAD"))
	public void onDropSelectedItem(boolean all, CallbackInfoReturnable<ItemEntity> callbackInfoReturnable) {
		SlotRefiller.set(inventory, getMainHandStack().copy());
	}

	@Inject(method = "dropSelectedItem", at = @At("RETURN"))
	public void onSelectedItemDropped(boolean all, CallbackInfoReturnable<ItemEntity> callbackInfoReturnable) {
		if(getMainHandStack().isEmpty()) {
			SlotRefiller.refill();
		}
	}
}
