package com.psclistens.example.jsf;

/**
 * This class extends SelectItem, but implements equals & hashCode (so duplicates can be eliminated by putting them in a
 * set) and compareTo (so they can be sorted.) Nulls will sort to the top.
 * 
 * @author LYNCHNF
 */
public class SelectItem extends javax.faces.model.SelectItem implements Comparable<SelectItem> {
    private static final long serialVersionUID = 1L;

    public SelectItem() {}

    public SelectItem(Object value) {
        super(value);
    }

    public SelectItem(Object value, String label) {
        super(value, label);
    }

    public SelectItem(Object value, String label, String description) {
        super(value, label, description);
    }

    public SelectItem(Object value, String label, String description, boolean disabled) {
        super(value, label, description, disabled);
    }

    public SelectItem(Object value, String label, String description, boolean disabled, boolean escape) {
        super(value, label, description, disabled, escape);
    }

    public SelectItem(Object value, String label, String description, boolean disabled, boolean escape, boolean noSelectionOption) {
        super(value, label, description, disabled, escape, noSelectionOption);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectItem that = (SelectItem) o;

        if (getLabel() != null ? !getLabel().equals(that.getLabel()) : that.getLabel() != null) return false;
        if (getValue() != null ? !getValue().equals(that.getValue()) : that.getValue() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getValue() != null ? getValue().hashCode() : 0;
        result = 31 * result + (getLabel() != null ? getLabel().hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(SelectItem o) {
        if (this.equals(o)) return 0;
        if (getLabel() == null && o.getLabel() != null) return -1;
        if (getLabel() != null && o.getLabel() == null) return 1;
        if (getLabel() != null && o.getLabel() != null && !getLabel().equals(o.getLabel())) return getLabel().compareTo(o.getLabel());

        if (getValue() == null && o.getValue() != null) return -1;
        if (getValue() != null && o.getValue() == null) return 1;

        return 0;
    }
}