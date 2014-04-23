package com.psclistens.example.jsf.item;

import java.io.Serializable;

import com.psclistens.example.domain.Item;

/**
 * This class adds a boolean check box to the item entity.
 * 
 * @author LYNCHNF
 */
public class ItemRow implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean checked;
    private Item item;

    public ItemRow(Item item) {
        this.item = item;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ItemRow [checked=");
        builder.append(checked);
        builder.append(", ");
        if (item != null) {
            builder.append("item=");
            builder.append(item);
        }
        builder.append("]");
        return builder.toString();
    }
}