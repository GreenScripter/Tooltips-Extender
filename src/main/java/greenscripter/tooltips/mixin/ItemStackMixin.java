package greenscripter.tooltips.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import greenscripter.tooltips.TooltipExtender;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	
	@Inject(method = "isSectionVisible", at = @At("HEAD"), cancellable = true)
	private static void isSectionVisible(int flags, ItemStack.TooltipSection tooltipSection, CallbackInfoReturnable<Boolean> ci) {
		ci.setReturnValue(true);
	}
	
	@Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
	private void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> ci) {
		List<Text> lines = ci.getReturnValue();
		List<Text> linesTail = new ArrayList<>();
		ItemStack is = ((ItemStack) (Object) this);
		
		if (context.isAdvanced()) {
			if (is.isDamaged()) {
				linesTail.add(0, lines.remove(lines.size() - 1));
			}
			linesTail.add(0, lines.remove(lines.size() - 1));
			if (is.hasNbt()) {
				linesTail.add(0, lines.remove(lines.size() - 1));
			}
		}
		if (is.getItem() instanceof BlockItem && ((BlockItem) is.getItem()).getBlock() instanceof ShulkerBoxBlock) {
			Text name = lines.get(0);
			lines.clear();
			lines.add(name);
		}
		lines.addAll(TooltipExtender.extendTooltips(is, player, context));
		int hide = getHideFlags();
		if (hide != 0) {
			lines.add(new LiteralText("Hide Flags: " + hide).formatted(Formatting.GRAY));
		}
		lines.addAll(linesTail);
		ci.setReturnValue(lines);
	}
	
	@Shadow
	private int getHideFlags() {
		return 0;
	}
	
}
