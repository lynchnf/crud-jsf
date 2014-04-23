package com.psclistens.example.jsf;

import java.util.Currency;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.NumberConverter;

import org.apache.commons.lang.StringUtils;

/**
 * This custom converter wraps a standard javax.faces.numberConverter, but it allows numbers to be input with or without
 * a leading currency symbol (e.g. $12.34 or 12.34). To use, see the example below:
 * 
 * <pre>
 *     &lt;h:inputText value="#{backing.price}" label="Price"&gt;
 *         &lt;f:converter converterId="com.psclistens.currency" /&gt;
 *         &lt;f:attribute name="currencySymbol" value="$" /&gt;
 *         &lt;f:attribute name="groupingUsed" value="true" /&gt;
 *         &lt;f:attribute name="integerOnly" value="false" /&gt;
 *         &lt;f:attribute name="locale" value="en_US" /&gt;
 *         &lt;f:attribute name="maxFractionDigits" value="4" /&gt;
 *         &lt;f:attribute name="maxIntegerDigits" value="7" /&gt;
 *         &lt;f:attribute name="minFractionDigits" value="2" /&gt;
 *         &lt;f:attribute name="minIntegerDigits" value="1" /&gt;
 *     &lt;/h:inputText&gt;
 * </pre>
 * 
 * @author LYNCHNF
 * @see javax.faces.convert.NumberConverter
 */
@FacesConverter("com.psclistens.currency")
public class CurrencyConverter implements Converter {
    private NumberConverter converter = new NumberConverter();

    public Object getAsObject(FacesContext context, UIComponent component, String newValue) {
        if (newValue == null) return null;
        newValue = newValue.trim();
        if (newValue.length() <= 0) return null;
        converter.setType("currency");
        attributesToConverter(component);
        String origValue = newValue;
        String symbol = converter.getCurrencySymbol();
        if (symbol == null) {
            symbol = Currency.getInstance(converter.getLocale()).getSymbol();
        }
        if (!newValue.startsWith(symbol)) newValue = symbol + newValue;
        try {
            return converter.getAsObject(context, component, newValue);
        } catch (ConverterException e) {
            Severity severity = e.getFacesMessage().getSeverity();
            String summary = e.getFacesMessage().getSummary();
            String detail = e.getFacesMessage().getDetail();
            summary = StringUtils.replace(summary, newValue, origValue);
            detail = StringUtils.replace(detail, newValue, origValue);
            FacesMessage facesMessage = new FacesMessage(severity, summary, detail);
            throw new ConverterException(facesMessage, e);
        }
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) return "";
        if (value instanceof String) return (String) value;
        converter.setType("currency");
        attributesToConverter(component);
        return converter.getAsString(context, component, value);
    }

    private void attributesToConverter(UIComponent component) {

        // Get Locale and set on converter.
        String localeRaw = (String) component.getAttributes().get("locale");
        if (StringUtils.isNotBlank(localeRaw)) {
            Locale locale = new Locale(localeRaw);
            converter.setLocale(locale);
        }

        // Get and set currency symbol.
        String currencySymbol = (String) component.getAttributes().get("currencySymbol");
        if (StringUtils.isNotBlank(currencySymbol)) {
            converter.setCurrencySymbol(currencySymbol);
        }

        // Get and set grouping used flag.
        String groupingUsedRaw = (String) component.getAttributes().get("groupingUsed");
        if (StringUtils.isNotBlank(groupingUsedRaw)) {
            Boolean groupingUsed = Boolean.valueOf(groupingUsedRaw);
            converter.setGroupingUsed(groupingUsed.booleanValue());
        }

        // Get and set integerOnly flag.
        String integerOnlyRaw = (String) component.getAttributes().get("integerOnly");
        if (StringUtils.isNotBlank(integerOnlyRaw)) {
            Boolean integerOnly = Boolean.valueOf(integerOnlyRaw);
            converter.setIntegerOnly(integerOnly.booleanValue());
        }

        // Get and set maxFractionDigits number.
        String maxFractionDigitsRaw = (String) component.getAttributes().get("maxFractionDigits");
        if (StringUtils.isNotBlank(maxFractionDigitsRaw)) {
            Integer maxFractionDigits = Integer.valueOf(maxFractionDigitsRaw);
            converter.setMaxFractionDigits(maxFractionDigits.intValue());
        }

        // Get and set maxIntegerDigits number.
        String maxIntegerDigitsRaw = (String) component.getAttributes().get("maxIntegerDigits");
        if (StringUtils.isNotBlank(maxIntegerDigitsRaw)) {
            Integer maxIntegerDigits = Integer.valueOf(maxIntegerDigitsRaw);
            converter.setMaxIntegerDigits(maxIntegerDigits.intValue());
        }

        // Get and set minFractionDigits number.
        String minFractionDigitsRaw = (String) component.getAttributes().get("minFractionDigits");
        if (StringUtils.isNotBlank(minFractionDigitsRaw)) {
            Integer minFractionDigits = Integer.valueOf(minFractionDigitsRaw);
            converter.setMinFractionDigits(minFractionDigits.intValue());
        }

        // Get and set minIntegerDigits number.
        String minIntegerDigitsRaw = (String) component.getAttributes().get("minIntegerDigits");
        if (StringUtils.isNotBlank(minIntegerDigitsRaw)) {
            Integer minIntegerDigits = Integer.valueOf(minIntegerDigitsRaw);
            converter.setMinIntegerDigits(minIntegerDigits.intValue());
        }
    }
}