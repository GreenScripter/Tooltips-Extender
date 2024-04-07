package greenscripter.tooltips;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(value = EnvType.CLIENT)
public class ShulkerTooltipComponent implements TooltipComponent {

	public static final Identifier TEXTURE = new Identifier("container/bundle/background");
	private final DefaultedList<ItemStack> inventory;

	public ShulkerTooltipComponent(ShulkerTooltipData data) {
		this.inventory = data.getInventory();
	}

	@Override
	public int getHeight() {
		return this.getRows() * 20 + 2 ;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return this.getColumns() * 18 + 2;
	}

	private int getColumns() {
		return 9;
	}

	private int getRows() {
		return 3;
	}


    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        int i = this.getColumns();
        int j = this.getRows();
        context.drawGuiTexture(TEXTURE, x, y, this.getWidth(textRenderer), this.getHeight());
        boolean bl = false;
        int k = 0;
        for (int l = 0; l < j; ++l) {
            for (int m = 0; m < i; ++m) {
                int n = x + m * 18 + 1;
                int o = y + l * 20 + 1;
                this.drawSlot(n, o, k++, bl, context, textRenderer);
            }
        }
    }

    private void drawSlot(int x, int y, int index, boolean shouldBlock, DrawContext context, TextRenderer textRenderer) {
        ItemStack itemStack = this.inventory.get(index);
        this.draw(context, x, y, Sprite.SLOT);
        context.drawItem(itemStack, x + 1, y + 1, index);
        context.drawItemInSlot(textRenderer, itemStack, x + 1, y + 1);
    }

    private void draw(DrawContext context, int x, int y, Sprite sprite) {
        context.drawGuiTexture(sprite.texture, x, y, 0, sprite.width, sprite.height);
    }


    @Environment(value=EnvType.CLIENT)
    static enum Sprite {
        BLOCKED_SLOT(new Identifier("container/bundle/blocked_slot"), 18, 20),
        SLOT(new Identifier("container/bundle/slot"), 18, 20);

        public final Identifier texture;
        public final int width;
        public final int height;

        private Sprite(Identifier texture, int width, int height) {
            this.texture = texture;
            this.width = width;
            this.height = height;
        }
    }
}
