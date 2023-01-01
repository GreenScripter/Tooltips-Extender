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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Mixin(Screen.class)
public class TooltipComponentMixin {

	@Inject(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V", at = @At("HEAD"), cancellable = true)
	private void renderTooltip(MatrixStack matrices, List<Text> lines, Optional<TooltipData> data2, int x, int y, CallbackInfo ci) {
		List<TooltipComponent> list = lines.stream().map(Text::asOrderedText).map(o -> TooltipComponent.of(o)).collect(Collectors.toList());
		TooltipData d = data2.orElse(null);
		if (d != null && d instanceof ShulkerTooltipData) {
			list.add(new ShulkerTooltipComponent((ShulkerTooltipData) d));
		} else {
			data2.ifPresent(data -> list.add(1, TooltipComponent.of(data)));
		}
		this.renderTooltipFromComponents(matrices, list, x, y, HoveredTooltipPositioner.INSTANCE);
		ci.cancel();
	}

	@Shadow
	private void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner) {}

}
