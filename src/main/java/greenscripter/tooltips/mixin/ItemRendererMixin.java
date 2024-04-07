package greenscripter.tooltips.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import greenscripter.tooltips.EnchantmentLabeller;
import greenscripter.tooltips.ItemOverlay;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Inject(method = "renderItem", at = @At("RETURN"), cancellable = true)
	public void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
		ItemOverlay.renderItem(stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, model, ci);
	}

	//	@Inject(method = "renderItem", at = @At("RETURN"), cancellable = true)
	//	private void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
	//		if (!renderMode.equals(ModelTransformationMode.GUI)) {
	//			return;
	//		}
	//
	//		if (stack.isOf(Items.ENCHANTED_BOOK)) {
	//			TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
	//			matrices.push();
	//			matrices.scale(0.04f, -0.04f, 0.04f);
	//			matrices.translate(-12, 0, 3);
	//			NbtList l = stack.getNbt().getList("StoredEnchantments", NbtType.COMPOUND);
	//			matrices.translate(0, (l.size() > 3 ? renderer.fontHeight : renderer.fontHeight * l.size()) / -2f, 0);
	//
	//			if (l.size() > 3) {
	//				renderer.draw(matrices, l.size() + "", 1, 1, 0);
	//
	//				renderer.draw(matrices, l.size() + "", 0, 0, 0x00FFFF);
	//
	//			} else {
	//
	//				for (int i = 0; i < l.size(); i++) {
	//					NbtCompound ench = l.getCompound(i);
	//
	//					String id = ench.getString("id").replace("minecraft:", "");
	//					String name = EnchantmentLabeller.names.get(ench.getString("id").replace("minecraft:", "")) + "";
	//
	//					short level = ench.getShort("lvl");
	//					int maxLevel = EnchantmentLabeller.levels.getOrDefault(id, 1);
	//
	//					float f = Math.max(0.0f, ((float) maxLevel - (float) level) / (float) maxLevel);
	//					int color = MathHelper.hsvToRgb(0.3f - f * 0.4f, 1.0f, 1.0f);
	//
	//					if (EnchantmentLabeller.curses.contains(id)) {
	//						color = 0xFF0000;
	//					}
	//
	//					String render = name;
	//					if (maxLevel != 1) {
	//						render += " " + level;
	//					}
	//
	//					renderer.draw(matrices, render, 1, renderer.fontHeight * i + 1, 0);
	//
	//					renderer.draw(matrices, render, 0, renderer.fontHeight * i, color);
	//
	//				}
	//			}
	//			matrices.pop();
	//
	//		}
	//
	//	}

}
