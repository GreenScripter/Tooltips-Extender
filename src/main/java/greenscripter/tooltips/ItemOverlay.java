package greenscripter.tooltips;

import java.util.List;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ItemOverlay {

	public static void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
		if (renderMode.equals(ModelTransformationMode.FIRST_PERSON_LEFT_HAND) || renderMode.equals(ModelTransformationMode.FIRST_PERSON_RIGHT_HAND)) {
			return;
		}
		if (!stack.isOf(Items.ENCHANTED_BOOK) && !stack.isOf(Items.SUSPICIOUS_STEW)) {
			return;
		}
		matrices.push();
		for (int ii = 0; ii < 2; ii++) {
			TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
			matrices.push();
			matrices.scale(0.04f, -0.04f, 0.04f);
			if (renderMode.equals(ModelTransformationMode.GROUND)) {
				matrices.scale(0.5f, 0.5f, 0.5f);
				matrices.translate(0, -4, 0);
			}
			if (renderMode.equals(ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) || renderMode.equals(ModelTransformationMode.THIRD_PERSON_LEFT_HAND)) {
				matrices.scale(0.5f, 0.5f, 0.5f);
				matrices.translate(0, -10, 3);
			}
			matrices.translate(-12, 0, 2);
			if (stack.isOf(Items.SUSPICIOUS_STEW)) {
				List<StatusEffectInstance> effects = TooltipExtender.getStewPotionEffects(stack.getNbt());
				if (effects.size() == 1) {
					matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
					int r = 255;
					int g = 100;
					int b = 100;
					if (effects.get(0).getEffectType().isBeneficial()) {
						g = 255;
						r = 100;
					}
//					int color = effects.get(0).getEffectType().getColor();
//					r = (color >> 16) & 255;
//					g = (color >> 8) & 255;
//					b = (color) & 255;
					VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getDebugSectionQuads());
					Matrix4f m = matrices.peek().getPositionMatrix();
					vc.vertex(m, -25f, 12f, 3).color(r, g, b, 127).next();
					vc.vertex(m, -25, -12, 3).color(r, g, b, 127).next();
					vc.vertex(m, 0, -12, 3).color(r, g, b, 127).next();
					vc.vertex(m, 0, 12, 3).color(r, g, b, 127).next();
				}
			}
			if (stack.isOf(Items.ENCHANTED_BOOK)) {

				NbtList l = stack.getNbt().getList("StoredEnchantments", 10);
				matrices.translate(0, (l.size() > 3 ? renderer.fontHeight : renderer.fontHeight * l.size()) / -2f, 0);

				if (l.size() > 3) {
					renderer.draw(l.size() + "", 0, 0, 0x00FFFF, true, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

				} else {

					for (int i = 0; i < l.size(); i++) {
						NbtCompound ench = l.getCompound(i);

						String id = ench.getString("id").replace("minecraft:", "");
						String name = EnchantmentLabeller.names.get(ench.getString("id").replace("minecraft:", "")) + "";

						short level = ench.getShort("lvl");
						int maxLevel = EnchantmentLabeller.levels.getOrDefault(id, 1);

						float f = Math.max(0.0f, ((float) maxLevel - (float) level) / (float) maxLevel);
						int color = MathHelper.hsvToRgb(0.3f - f * 0.4f, 1.0f, 1.0f);

						if (EnchantmentLabeller.curses.contains(id)) {
							color = 0xFF0000;
						}

						String render = name;
						if (maxLevel != 1) {
							render += " " + level;
						}
						//						renderer.draw(render, 1, renderer.fontHeight * i + 1, 0, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
						renderer.draw(render, 0, renderer.fontHeight * i, color, true, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

						//					renderer.draw(matrices, render, 1, renderer.fontHeight * i + 1, 0);

						//					renderer.draw(matrices, render, 0, renderer.fontHeight * i, color);

					}
				}

			}
			matrices.pop();
			if (renderMode.equals(ModelTransformationMode.GUI)) {
				break;
			}
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
			if (renderMode.equals(ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) || renderMode.equals(ModelTransformationMode.THIRD_PERSON_LEFT_HAND)) {
				matrices.translate(0, 0, -0.13);
			}
			if (renderMode.equals(ModelTransformationMode.GROUND)) {
			}

		}
		matrices.pop();
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
