package greenscripter.tooltips.mixin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import greenscripter.tooltips.ShulkerTooltipComponent;
import greenscripter.tooltips.ShulkerTooltipData;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.item.TooltipData;
import net.minecraft.text.Text;

@Mixin(DrawContext.class)
public class TooltipComponentMixin {

	@Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V", at = @At("HEAD"), cancellable = true)
	private void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data2, int x, int y, CallbackInfo ci) {
		List<TooltipComponent> list = text.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
		TooltipData d = data2.orElse(null);
		if (d != null && d instanceof ShulkerTooltipData) {
			list.add(new ShulkerTooltipComponent((ShulkerTooltipData) d));
		} else {
			data2.ifPresent(data -> list.add(1, TooltipComponent.of(data)));
		}

		this.drawTooltip(textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE);
		ci.cancel();
	}

	@Shadow
	private void drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner) {}
	
}
