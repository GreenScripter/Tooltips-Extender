package greenscripter.tooltips;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.world.ClientWorld;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class TooltipExtender {
	
	public static List<Text> extendTooltips(ItemStack is, PlayerEntity player, TooltipContext context) {
		List<Text> lines = new ArrayList<>();
		if (is.getItem().equals(Items.SUSPICIOUS_STEW)) {
			buildStewTooltip(is, lines, 1.0F);
		}
		if (is.getItem().equals(Items.COMPASS) && is.getNbt() != null) {
			NbtCompound nbt = is.getNbt();
			if (nbt.contains("LodestoneDimension", NbtElement.STRING_TYPE)) {
				lines.add(new LiteralText("Dimension: " + nbt.getString("LodestoneDimension")).formatted(Formatting.GRAY));
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
				lines.add(new LiteralText("Location: " + target).formatted(Formatting.GRAY));
			}
			if (nbt.contains("LodestoneTracked") && !nbt.getBoolean("LodestoneTracked")) {
				lines.add(new LiteralText("Untracked").formatted(Formatting.GRAY));
			}
		}
		if (is.getItem().equals(Items.COMPASS) && is.getNbt() == null) {
			@SuppressWarnings("resource")
			ClientWorld you = MinecraftClient.getInstance().world;
			if (you != null) {
				BlockPos pos = you.getSpawnPos();
				if (pos == null) {
					lines.add(new LiteralText("Untracked").formatted(Formatting.GRAY));
				} else {
					lines.add(new LiteralText("Location: " + pos.getX() + " " + pos.getY() + " " + pos.getZ()).formatted(Formatting.GRAY));
				}
			}
		}
		if (is.getItem().equals(Items.WRITTEN_BOOK) && is.getNbt() != null) {
			NbtCompound nbt = is.getNbt();
			int pages = WrittenBookItem.getPageCount(is);
			lines.add(new LiteralText("Page Count: " + pages).formatted(Formatting.GRAY));
			lines.add(new LiteralText("Opened: " + (nbt.getByte("resolved") == 1 ? "true" : "false")).formatted(Formatting.GRAY));
		}
		if (is.getItem() instanceof HorseArmorItem) {
			int bonus = ((HorseArmorItem) is.getItem()).getBonus();
			lines.add(new LiteralText(""));
			lines.add(new LiteralText("When on Horse:").formatted(Formatting.GRAY));
			lines.add(new LiteralText("+" + bonus + " Armor").formatted(Formatting.BLUE));
		}
		if (is.getItem() instanceof ToolItem) {
			float speed = ((ToolItem) is.getItem()).getMaterial().getMiningSpeedMultiplier();
			int level = ((ToolItem) is.getItem()).getMaterial().getMiningLevel();
			final String[] miningLevels = { "Stone", "Iron", "Diamond", "Obsidian", "Obsidian+" };
			String levelText = level < miningLevels.length ? miningLevels[level] : level + "";
			lines.add(new LiteralText(String.format(" %.1f", speed) + " Mining Speed").formatted(Formatting.DARK_GREEN));
			lines.add(new LiteralText(" " + levelText + " Mining Level").formatted(Formatting.DARK_GREEN));
		}
		if (is.getItem().equals(Items.CLOCK) && is.getNbt() == null) {
			@SuppressWarnings("resource")
			ClientWorld you = MinecraftClient.getInstance().world;
			if (you != null) {
				long time = you.getTimeOfDay() % 24000;
				
				lines.add(new LiteralText("Time: " + ((time / 1000) + 6) % 24 + ":" + String.format("%02d", (int) (time % 1000 / (1000 / 60.0)))).formatted(Formatting.GRAY));
			}
		}
		if ((is.getItem().equals(Items.BEEHIVE) || is.getItem().equals(Items.BEE_NEST)) && is.getNbt() != null) {
			NbtCompound nbt = is.getNbt();
			if (nbt.contains("BlockEntityTag", NbtElement.COMPOUND_TYPE)) {
				NbtCompound block = nbt.getCompound("BlockEntityTag");
				if (block.contains("Bees", NbtElement.LIST_TYPE)) {
					lines.add(new LiteralText("Bee Count: " + block.getList("Bees", NbtElement.COMPOUND_TYPE).size()).formatted(Formatting.GRAY));
				}
			}
			if (nbt.contains("BlockStateTag", NbtElement.COMPOUND_TYPE)) {
				NbtCompound block = nbt.getCompound("BlockStateTag");
				if (block.contains("honey_level", NbtElement.INT_TYPE)) {
					lines.add(new LiteralText("Honey Level: " + block.getInt("honey_level")).formatted(Formatting.GRAY));
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
			lines.add(new LiteralText("Hunger: " + food.getHunger()).formatted(Formatting.GRAY));
			lines.add(new LiteralText("Saturation: " + String.format("%.1f", ((float) food.getHunger() * food.getSaturationModifier() * 2.0f))).formatted(Formatting.GRAY));
		}
		if (is.getRepairCost() > 0) {
			lines.add(new LiteralText("Repair Cost: " + is.getRepairCost()).formatted(Formatting.GRAY));
		}
		return lines;
	}
	
	private static void buildFishTooltip(ItemStack stack, List<Text> lines) {
		NbtCompound nbt = stack.getNbt();
		
		if (nbt != null) {
			if (nbt.contains("NoAI") && nbt.getBoolean("NoAI")) {
				lines.add(new LiteralText("No AI").formatted(Formatting.GRAY));
			}
			if (nbt.contains("Silent") && nbt.getBoolean("Silent")) {
				lines.add(new LiteralText("Silent").formatted(Formatting.GRAY));
			}
			if (nbt.contains("NoGravity") && nbt.getBoolean("NoGravity")) {
				lines.add(new LiteralText("No Gravity").formatted(Formatting.GRAY));
			}
			if (nbt.contains("Glowing") && nbt.getBoolean("Glowing")) {
				lines.add(new LiteralText("Glowing").formatted(Formatting.GRAY));
			}
			if (nbt.contains("Invulnerable") && nbt.getBoolean("Invulnerable")) {
				lines.add(new LiteralText("Invulnerable").formatted(Formatting.GRAY));
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
				lines.add(new LiteralText(name).formatted(Formatting.GRAY));
			}
			if (nbt.contains("Health", 99)) {
				lines.add(new LiteralText("Health: " + String.format("%.1f", nbt.getFloat("Health"))).formatted(Formatting.GRAY));
			}
			if (nbt.contains("Age", NbtElement.INT_TYPE)) {
				int age = nbt.getInt("Age");
				if (age >= 0) {
					lines.add(new LiteralText("Adult").formatted(Formatting.GRAY));
				} else {
					int minutes = -age / 20 / 60;
					int seconds = (-age / 20) % 60;
					lines.add(new LiteralText("Baby: " + minutes + ":" + seconds).formatted(Formatting.GRAY));
				}
			}
		}
	}
	
	private static void buildStewTooltip(ItemStack stack, List<Text> list, float durationMultiplier) {
		MutableText mutableText;
		List<StatusEffectInstance> list2 = new ArrayList<>();
		getStewPotionEffects(stack.hasNbt() ? stack.getNbt() : null, list2);
		ArrayList<Pair<EntityAttribute, EntityAttributeModifier>> list3 = Lists.newArrayList();
		if (list2.isEmpty()) {
			list.add((new TranslatableText("effect.none")).formatted(Formatting.GRAY));
		} else {
			for (StatusEffectInstance statusEffectInstance : list2) {
				mutableText = new TranslatableText(statusEffectInstance.getTranslationKey());
				StatusEffect statusEffect = statusEffectInstance.getEffectType();
				Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
				if (!map.isEmpty()) {
					for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
						EntityAttributeModifier entityAttributeModifier = entry.getValue();
						EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(entityAttributeModifier.getName(), statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
						list3.add(new Pair<EntityAttribute, EntityAttributeModifier>(entry.getKey(), entityAttributeModifier2));
					}
				}
				if (statusEffectInstance.getAmplifier() > 0) {
					mutableText = new TranslatableText("potion.withAmplifier", mutableText, new TranslatableText("potion.potency." + statusEffectInstance.getAmplifier()));
				}
				if (statusEffectInstance.getDuration() > 20) {
					mutableText = new TranslatableText("potion.withDuration", mutableText, StatusEffectUtil.durationToString(statusEffectInstance, durationMultiplier));
				}
				list.add(mutableText.formatted(statusEffect.getCategory().getFormatting()));
			}
		}
		if (!list3.isEmpty()) {
			list.add(new LiteralText(""));
			list.add(new TranslatableText("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
			for (Pair<EntityAttribute, EntityAttributeModifier> pair : list3) {
				EntityAttributeModifier modifier = (EntityAttributeModifier) pair.getSecond();
				double statusEffect = ((EntityAttributeModifier) modifier).getValue();
				double d = ((EntityAttributeModifier) modifier).getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || ((EntityAttributeModifier) modifier).getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL ? ((EntityAttributeModifier) modifier).getValue() * 100.0 : ((EntityAttributeModifier) modifier).getValue();
				if (statusEffect > 0.0) {
					list.add(new TranslatableText("attribute.modifier.plus." + ((EntityAttributeModifier) modifier).getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(d), new TranslatableText(((EntityAttribute) pair.getFirst()).getTranslationKey())).formatted(Formatting.BLUE));
					continue;
				}
				if (!(statusEffect < 0.0)) continue;
				list.add(new TranslatableText("attribute.modifier.take." + ((EntityAttributeModifier) modifier).getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(d *= -1.0), new TranslatableText(((EntityAttribute) pair.getFirst()).getTranslationKey())).formatted(Formatting.RED));
			}
		}
	}
	
	private static void getStewPotionEffects(@Nullable NbtCompound nbt, List<StatusEffectInstance> list) {
		NbtCompound nbtCompound = nbt;
		if (nbtCompound != null && nbtCompound.contains("Effects", 9)) {
			NbtList nbtList = nbtCompound.getList("Effects", 10);
			
			for (int i = 0; i < nbtList.size(); ++i) {
				int j = 160;
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				if (nbtCompound2.contains("EffectDuration", 3)) {
					j = nbtCompound2.getInt("EffectDuration");
				}
				
				StatusEffect statusEffect = StatusEffect.byRawId(nbtCompound2.getByte("EffectId"));
				if (statusEffect != null) {
					list.add(new StatusEffectInstance(statusEffect, j));
				}
			}
		}
		
	}
}
