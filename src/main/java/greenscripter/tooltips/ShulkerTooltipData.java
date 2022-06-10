package greenscripter.tooltips;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class ShulkerTooltipData implements TooltipData {
	
	private final DefaultedList<ItemStack> inventory;
	private final int bundleOccupancy;
	
	public ShulkerTooltipData(DefaultedList<ItemStack> inventory, int bundleOccupancy) {
		this.inventory = inventory;
		this.bundleOccupancy = bundleOccupancy;
	}
	
	public DefaultedList<ItemStack> getInventory() {
		return this.inventory;
	}
	
	public int getBundleOccupancy() {
		return this.bundleOccupancy;
	}
}
