package greenscripter.tooltips;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.attribute.AttributeModifierCreator;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

public class TooltipExtender {

	public static List<Text> extendTooltips(ItemStack is, PlayerEntity player, TooltipContext context) {
		List<Text> lines = new ArrayList<>();
		if (is.getItem().equals(Items.SUSPICIOUS_STEW)) {
			buildStewTooltip(is, lines, 1.0F, 20f);
		}
		if (is.getItem().equals(Items.COMPASS) && is.getNbt() != null) {
			NbtCompound nbt = is.getNbt();
			if (nbt.contains("LodestoneDimension", NbtElement.STRING_TYPE)) {
				lines.add(Text.literal("Dimension: " + nbt.getString("LodestoneDimension")).formatted(Formatting.GRAY));
			}
			if (nbt.contains("LodestonePos", NbtElement.COMPOUND_TYPE)) {
				NbtCompound pos = nbt.getCompound("LodestonePos");
				String target = "";
				if (pos.contains("X", NbtElement.INT_TYPE)) {
					target += pos.getInt("X") + " ";
				}
				if (pos.contains("Y", NbtElement.INT_TYPE)) {
					target += pos.getInt("Y") + " ";
				}
				if (pos.contains("Z", NbtElement.INT_TYPE)) {
					target += pos.getInt("Z") + " ";
				}
				lines.add(Text.literal("Location: " + target).formatted(Formatting.GRAY));
			}
			if (nbt.contains("LodestoneTracked") && !nbt.getBoolean("LodestoneTracked")) {
				lines.add(Text.literal("Untracked").formatted(Formatting.GRAY));
			}
		}
		if (is.getItem().equals(Items.RECOVERY_COMPASS)) {
			@SuppressWarnings("resource")
			ClientPlayerEntity you = MinecraftClient.getInstance().player;
			if (you != null) {
				GlobalPos pos = you.getLastDeathPos().orElse(null);
				if (pos == null) {
					lines.add(Text.literal("Untracked").formatted(Formatting.GRAY));
				} else {
					lines.add(Text.literal("Dimension: " + pos.getDimension().getValue()).formatted(Formatting.GRAY));
					lines.add(Text.literal("Location: " + pos.getPos().getX() + " " + pos.getPos().getY() + " " + pos.getPos().getZ()).formatted(Formatting.GRAY));
				}
			}
		}
		if (is.getItem().equals(Items.COMPASS) && is.getNbt() == null) {
			@SuppressWarnings("resource")
			ClientWorld you = MinecraftClient.getInstance().world;
			if (you != null) {
				BlockPos pos = you.getSpawnPos();
				if (pos == null) {
					lines.add(Text.literal("Untracked").formatted(Formatting.GRAY));
				} else {
					lines.add(Text.literal("Location: " + pos.getX() + " " + pos.getY() + " " + pos.getZ()).formatted(Formatting.GRAY));
				}
			}
		}
		if (is.getItem().equals(Items.WRITTEN_BOOK) && is.getNbt() != null) {
			NbtCompound nbt = is.getNbt();
			int pages = WrittenBookItem.getPageCount(is);
			lines.add(Text.literal("Page Count: " + pages).formatted(Formatting.GRAY));
			lines.add(Text.literal("Opened: " + (nbt.getByte("resolved") == 1 ? "true" : "false")).formatted(Formatting.GRAY));
		}
		if (is.getItem() instanceof HorseArmorItem) {
			int bonus = ((HorseArmorItem) is.getItem()).getBonus();
			lines.add(Text.literal(""));
			lines.add(Text.literal("When on Horse:").formatted(Formatting.GRAY));
			lines.add(Text.literal("+" + bonus + " Armor").formatted(Formatting.BLUE));
		}
		if (is.getItem() instanceof ToolItem) {
			float speed = ((ToolItem) is.getItem()).getMaterial().getMiningSpeedMultiplier();
			int level = ((ToolItem) is.getItem()).getMaterial().getMiningLevel();
			final String[] miningLevels = { "Stone", "Iron", "Diamond", "Obsidian", "Obsidian+" };
			String levelText = level < miningLevels.length ? miningLevels[level] : level + "";
			lines.add(Text.literal(String.format(" %.1f", speed) + " Mining Speed").formatted(Formatting.DARK_GREEN));
			lines.add(Text.literal(" " + levelText + " Mining Level").formatted(Formatting.DARK_GREEN));
		}
		if (is.getItem().equals(Items.CLOCK) && is.getNbt() == null) {
			@SuppressWarnings("resource")
			ClientWorld you = MinecraftClient.getInstance().world;
			if (you != null) {

				long time = you.getTimeOfDay() % 24000;

				lines.add(Text.literal("Time: " + ((time / 1000) + 6) % 24 + ":" + String.format("%02d", (int) (time % 1000 / (1000 / 60.0)))).formatted(Formatting.GRAY));
			}
		}
		if ((is.getItem().equals(Items.BEEHIVE) || is.getItem().equals(Items.BEE_NEST)) && is.getNbt() != null) {
			NbtCompound nbt = is.getNbt();
			if (nbt.contains("BlockEntityTag", NbtElement.COMPOUND_TYPE)) {
				NbtCompound block = nbt.getCompound("BlockEntityTag");
				if (block.contains("Bees", NbtElement.LIST_TYPE)) {
					lines.add(Text.literal("Bee Count: " + block.getList("Bees", NbtElement.COMPOUND_TYPE).size()).formatted(Formatting.GRAY));
				}
			}
			if (nbt.contains("BlockStateTag", NbtElement.COMPOUND_TYPE)) {
				NbtCompound block = nbt.getCompound("BlockStateTag");
				if (block.contains("honey_level", NbtElement.INT_TYPE)) {
					lines.add(Text.literal("Honey Level: " + block.getInt("honey_level")).formatted(Formatting.GRAY));
				}
			}
		}
		if (is.getNbt() != null) {
			//			System.out.println(is.getNbt().asString());
		}
		if (is.getItem() instanceof EntityBucketItem) {
			buildFishTooltip(is, lines);
		}
		if (is.getItem().getFoodComponent() != null) {
			FoodComponent food = is.getItem().getFoodComponent();
			lines.add(Text.literal("Hunger: " + food.getHunger()).formatted(Formatting.GRAY));
			lines.add(Text.literal("Saturation: " + String.format("%.1f", ((float) food.getHunger() * food.getSaturationModifier() * 2.0f))).formatted(Formatting.GRAY));
		}
		if (is.getRepairCost() > 0) {
			lines.add(Text.literal("Repair Cost: " + is.getRepairCost()).formatted(Formatting.GRAY));
		}
		return lines;
	}

	private static void buildFishTooltip(ItemStack stack, List<Text> lines) {
		NbtCompound nbt = stack.getNbt();

		if (nbt != null) {
			if (nbt.contains("NoAI") && nbt.getBoolean("NoAI")) {
				lines.add(Text.literal("No AI").formatted(Formatting.GRAY));
			}
			if (nbt.contains("Silent") && nbt.getBoolean("Silent")) {
				lines.add(Text.literal("Silent").formatted(Formatting.GRAY));
			}
			if (nbt.contains("NoGravity") && nbt.getBoolean("NoGravity")) {
				lines.add(Text.literal("No Gravity").formatted(Formatting.GRAY));
			}
			if (nbt.contains("Glowing") && nbt.getBoolean("Glowing")) {
				lines.add(Text.literal("Glowing").formatted(Formatting.GRAY));
			}
			if (nbt.contains("Invulnerable") && nbt.getBoolean("Invulnerable")) {
				lines.add(Text.literal("Invulnerable").formatted(Formatting.GRAY));
			}
			if (nbt.contains("Variant", NbtElement.INT_TYPE)) {
				int type = nbt.getInt("Variant");
				final String[] names = new String[] { "Pink", "White", "Yellow", "Brown", "Blue" };
				String name;
				if (type >= 0 && type < names.length) {
					name = names[type];
				} else {
					name = "Variant: " + type;
				}
				lines.add(Text.literal(name).formatted(Formatting.GRAY));
			}
			if (nbt.contains("Health", 99)) {
				lines.add(Text.literal("Health: " + String.format("%.1f", nbt.getFloat("Health"))).formatted(Formatting.GRAY));
			}
			if (nbt.contains("Age", NbtElement.INT_TYPE)) {
				int age = nbt.getInt("Age");
				if (age >= 0) {
					lines.add(Text.literal("Adult").formatted(Formatting.GRAY));
				} else {
					int minutes = -age / 20 / 60;
					int seconds = (-age / 20) % 60;
					lines.add(Text.literal("Baby: " + minutes + ":" + seconds).formatted(Formatting.GRAY));
				}
			}
		}
	}

	private static void buildStewTooltip(ItemStack stack, List<Text> list, float durationMultiplier, float tickRate) {
		List<StatusEffectInstance> statusEffects = getStewPotionEffects(stack.getNbt());
		ArrayList<Pair<EntityAttribute, EntityAttributeModifier>> list2 = Lists.newArrayList();
		if (statusEffects.isEmpty()) {
			list.add(Text.translatable("effect.none").formatted(Formatting.GRAY));
		} else {
			for (StatusEffectInstance statusEffectInstance : statusEffects) {
				MutableText mutableText = Text.translatable(statusEffectInstance.getTranslationKey());
				StatusEffect statusEffect = statusEffectInstance.getEffectType();
				Map<EntityAttribute, AttributeModifierCreator> map = statusEffect.getAttributeModifiers();
				if (!map.isEmpty()) {
					for (Map.Entry<EntityAttribute, AttributeModifierCreator> entry : map.entrySet()) {
						list2.add(new Pair<EntityAttribute, EntityAttributeModifier>(entry.getKey(), entry.getValue().createAttributeModifier(statusEffectInstance.getAmplifier())));
					}
				}
				if (statusEffectInstance.getAmplifier() > 0) {
					mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + statusEffectInstance.getAmplifier()));
				}
				if (!statusEffectInstance.isDurationBelow(20)) {
					mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(statusEffectInstance, durationMultiplier, tickRate));
				}
				list.add(mutableText.formatted(statusEffect.getCategory().getFormatting()));
			}
		}
		if (!list2.isEmpty()) {
			list.add(ScreenTexts.EMPTY);
			list.add(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
			for (Pair<?, ?> pair : list2) {
				EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier) pair.getSecond();
				double d = entityAttributeModifier.getValue();
				double e = entityAttributeModifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || entityAttributeModifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL ? entityAttributeModifier.getValue() * 100.0 : entityAttributeModifier.getValue();
				if (d > 0.0) {
					list.add(Text.translatable("attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e), Text.translatable(((EntityAttribute) pair.getFirst()).getTranslationKey())).formatted(Formatting.BLUE));
					continue;
				}
				if (!(d < 0.0)) continue;
				list.add(Text.translatable("attribute.modifier.take." + entityAttributeModifier.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e *= -1.0), Text.translatable(((EntityAttribute) pair.getFirst()).getTranslationKey())).formatted(Formatting.RED));
			}
		}

	}

	public static List<StatusEffectInstance> getStewPotionEffects(@Nullable NbtCompound nbt) {
		List<StatusEffectInstance> list = new ArrayList<>();
		NbtCompound nbtCompound = nbt;
		if (nbtCompound != null && nbtCompound.contains("effects", 9)) {
			NbtList nbtList = nbtCompound.getList("effects", 10);

			for (int i = 0; i < nbtList.size(); ++i) {
				int j = 160;
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				if (nbtCompound2.contains("duration", 3)) {
					j = nbtCompound2.getInt("duration");
				}
				StatusEffect statusEffect = Registries.STATUS_EFFECT.get(Identifier.tryParse(nbtCompound2.getString("id")));
				if (statusEffect != null) {
					list.add(new StatusEffectInstance(statusEffect, j));
				}
			}
		}
		return list;

	}
}
