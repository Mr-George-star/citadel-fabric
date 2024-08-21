package net.george.citadel.client.gui.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.george.citadel.Citadel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("unused")
public class LinkData {
    private String linked_page;
    private String text;
    private int x;
    private int y;
    private final int page;
    private final String item;
    private final String item_tag;

    public LinkData(String linkedPage, String titleText, int x, int y, int page) {
        this(linkedPage, titleText, x, y, page, null, null);
    }

    public LinkData(String linkedPage, String titleText, int x, int y, int page, String item, String itemTag) {
        this.linked_page = linkedPage;
        this.text = titleText;
        this.x = x;
        this.y = y;
        this.page = page;
        this.item = item;
        this.item_tag = itemTag;
    }

    public String getLinkedPage() {
        return this.linked_page;
    }

    public void setLinkedPage(String linkedPage) {
        this.linked_page = linkedPage;
    }

    public String getTitleText() {
        return this.text;
    }

    public void setTitleText(String titleText) {
        this.text = titleText;
    }

    public int getPage() {
        return this.page;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ItemStack getDisplayItem() {
        if (this.item == null || this.item.isEmpty()){
            return ItemStack.EMPTY;
        } else {
            ItemStack stack = new ItemStack(Registry.ITEM.get(new Identifier(this.item)));
            if (this.item_tag != null && !this.item_tag.isEmpty()) {
                NbtCompound nbt = null;
                try {
                    nbt = StringNbtReader.parse(this.item_tag);
                } catch (CommandSyntaxException exception) {
                    Citadel.LOGGER.catching(exception);
                }
                stack.setNbt(nbt);
            }
            return stack;
        }
    }
}
