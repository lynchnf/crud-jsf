package com.psclistens.example.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This logs ALL phases of the JSF lifecycle for debugging. Once you understand JSF completely (HA!), this class and the
 * faces-config.xml file can be deleted.
 * 
 * @author LYNCHNF
 */
public class PhaseLogger implements PhaseListener {
    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(PhaseLogger.class);

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    public void beforePhase(PhaseEvent event) {
        log.trace("---------- " + event.getPhaseId() + " --------------------");
    }

    public void afterPhase(PhaseEvent event) {
        log.trace("---------- " + event.getPhaseId() + " --------------------");
    }
}