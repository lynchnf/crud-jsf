package com.psclistens.example.jsf;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This is needed to keep Tomcat from changing nulls to zeros for numeric fields.
 * 
 * @author LYNCHNF
 */
public class CoerceToZero implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.setProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {}
}