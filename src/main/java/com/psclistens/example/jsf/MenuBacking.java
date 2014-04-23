package com.psclistens.example.jsf;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

import com.psclistens.example.crud.CrudException;

/**
 * This backing bean logs out the current user and then goes to the home page.
 * 
 * @author LYNCHNF
 */
@ManagedBean
public class MenuBacking implements Serializable {
    private static final long serialVersionUID = 1L;

    public String logOut() throws CrudException {
        FacesContextUtil.logOut();
        return null;
    }
}