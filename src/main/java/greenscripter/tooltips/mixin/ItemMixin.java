package greenscripter.tooltips.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import greenscripter.tooltips.ShulkerTooltipData;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipData;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

@Mixin(Item.class)
public class ItemMixin {
	
	@Inject(method = "getTooltipData", at = @At("RETURN"), cancellable = true)
	private void getTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> ci) {
		if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
			NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
			if (nbtCompound != null) {
				if (nbtCompound.contains("Items", 9)) {
					DefaultedList<ItemStack> items = DefaultedList.ofSize(27, ItemStack.EMPTY);
					Inventories.readNbt(nbtCompound, items);
					DefaultedList<ItemStack> result = DefaultedList.ofSize(27);
					for (int i = 0; i < 27; i++) {
						result.add(items.get(i));
					}
					
					ci.setReturnValue(Optional.of(new ShulkerTooltipData(result, 0)));
				}
			}
		}
	}
	
}
