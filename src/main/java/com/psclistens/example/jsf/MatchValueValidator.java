package com.psclistens.example.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;

/**
 * This validator will validate that two input components have the same value. It needs to go on a third (usually
 * hidden) component that comes after the two components to be compared. Example:
 * 
 * <pre>
 *     &lt;h:outputLabel value="Password" /&gt;
 *     &lt;h:inputSecret id="password" value="#{user.password}" /&gt;
 *     &lt;h:outputLabel value="Confirm password" /&gt;
 *     &lt;h:inputSecret id="confirmPassword" value="#{user.confirmPassword}" /&gt;
 *     &lt;h:inputHidden value="dummy-value" label="Passwords"&gt;
 *         &lt;f:validator validatorId="matchValueValidator" /&gt;
 *         &lt;f:attribute name="matchingComponentId1" value="password" /&gt;
 *         &lt;f:attribute name="matchingComponentId2" value="confirmPassword" /&gt;
 *     &lt;/h:inputHidden&gt;
 * </pre>
 */
@FacesValidator(value = "matchValueValidator")
public class MatchValueValidator implements Validator {
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String id1 = (String) component.getAttributes().get("matchingComponentId1");
        String id2 = (String) component.getAttributes().get("matchingComponentId2");
        UIInput component1 = (UIInput) component.findComponent(id1);
        UIInput component2 = (UIInput) component.findComponent(id2);
        Object value1 = component1.getLocalValue();
        Object value2 = component2.getLocalValue();

        // If values are strings, trim to null so blanks and nulls match.
        if (value1 instanceof String) {
            value1 = StringUtils.trimToNull((String) value1);
        }
        if (value2 instanceof String) {
            value2 = StringUtils.trimToNull((String) value2);
        }

        if (value1 == null && value2 != null || value1 != null && !value1.equals(value2)) {
            String label = (String) component.getAttributes().get("label");
            if (label == null) {
                label = "Fields";
            }
            String message = label + " not match";
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
        }
    }
}