package com.psclistens.example.jsf;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.psclistens.example.crud.CrudException;

/**
 * I wanted to put all the references to the FacesContext in a utility class to make the backing beans easier to unit
 * test. For testing, refactor this class to extract an interface, then inject a MockFacesContextUtil.
 * 
 * @author LYNCHNF
 */
public class FacesContextUtil {
    private static Log log = LogFactory.getLog(FacesContextUtil.class);

    public static void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    public static void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    public static Object getFromFlash(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getFlash().get(key);
    }

    public static String getRequestParameter(String name) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }

    public static String getUserName() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    public static boolean hasErrors() {
        Severity maxSev = FacesContext.getCurrentInstance().getMaximumSeverity();
        return maxSev != null && maxSev.compareTo(FacesMessage.SEVERITY_ERROR) >= 0;
    }

    public static boolean isUserInRole(String role) {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(role);
    }

    public static void logOut() throws CrudException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.invalidateSession();
        String contextPath = externalContext.getRequestContextPath();
        String redirectURL = externalContext.encodeRedirectURL(contextPath + "/", null);
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(redirectURL);
        } catch (IOException e) {
            String msg = "Error ocurred on log out.";
            log.error(msg, e);
            throw new CrudException(msg, e);
        }
    }

    public static void putInFlash(String key, Object value) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put(key, value);
    }
}