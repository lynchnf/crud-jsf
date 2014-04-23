package com.psclistens.example.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * This will convert string fields in the view to string fields in backing bean. If the view field ends with blanks,
 * they will be trimmed off. If the view field is completely blank, it will be converted to null.
 * 
 * @author LYNCHNF
 */
@FacesConverter(forClass=String.class)
public class TrimTrailingToNullConverter implements Converter {
    public Object getAsObject(FacesContext context, UIComponent component, String newValue) {
        if (newValue == null) return null;
        int len = newValue.length();
        while (len > 0 && newValue.charAt(len - 1) <= ' ') {
            len--;
        }
        if (len == 0) return null;
        if (len < newValue.length()) return newValue.substring(0, len);
        return newValue;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) return "";
        return String.valueOf(value);
    }
}