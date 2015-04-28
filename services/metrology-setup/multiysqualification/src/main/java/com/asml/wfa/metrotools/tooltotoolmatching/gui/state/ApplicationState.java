package com.asml.wfa.metrotools.tooltotoolmatching.gui.state;

import java.util.ArrayList;

import java.util.List;


import javax.enterprise.context.ApplicationScoped;

import com.asml.wfa.xml.adel.adelmetrology.AdelMetrology;

/**
 * Application state to preserve the state of the system
 * 
 */
@ApplicationScoped
public class ApplicationState {

    // TODO: This is only a skeleton, change it accordingly to reflect present state.

    // Full input list of candidate positions
    // Generated sample scheme

    // now hardcoded -> retrieve from ler
    private static final double WAFERDIAMETER = 300;

    private List<AdelMetrology> metrologyfiles;
 

    public ApplicationState() {
    	metrologyfiles=new ArrayList<AdelMetrology>();
    }

   

    public List<AdelMetrology> getMetrologyFiles() {
        return metrologyfiles;
    }

 

    public void setMetrologyFiles(final List<AdelMetrology> metrologyfiles) {
        this.metrologyfiles = metrologyfiles;
    }

    public void addMetrologyFile(final AdelMetrology metrologyfile) {
        this.metrologyfiles.add(metrologyfile);
    }

 

    public double getWaferDiameter() {
        return WAFERDIAMETER;
    }
}
