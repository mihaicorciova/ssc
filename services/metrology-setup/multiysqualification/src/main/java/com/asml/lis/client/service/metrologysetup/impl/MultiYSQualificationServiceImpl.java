/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asml.lis.client.service.metrologysetup.impl;

import com.asml.lis.client.service.metrologysetup.MultiYSQualificationService;
import com.asml.lis.client.service.metrologysetup.model.MachineData;
import com.asml.lis.client.service.metrologysetup.model.PlotData;
import com.asml.lis.client.service.metrologysetup.model.ProfileData;
import com.asml.lis.client.service.metrologysetup.util.AdelMetroDocumentReader;
import com.asml.wfa.xml.adel.adelmetrology.AdelMetrology;
import com.asml.wfa.xml.adel.adelmetrology.XmlMeasurement;
import com.asml.wfa.xml.adel.adelmetrology.XmlMetrologyWafer;
import com.asml.wfa.xml.adel.jaxb.XMLException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mihai Corciova <mihai.corciova@asml.com>
 */
public class MultiYSQualificationServiceImpl implements MultiYSQualificationService {
    
    @Override
    public Map<String, MachineData> parseMetrologyFiles(List<File> files) {
        
        Map<String, MachineData> md;
        md = new HashMap<>();
        List<PlotData> pl = new ArrayList<>();
        Map<String, ProfileData> pd = new HashMap<>();
        
        for (File file : files) {
            try (final InputStream stream = Files.newInputStream(file.toPath())) {
               
                AdelMetrology xml = AdelMetroDocumentReader.loadAdelMetrology(stream);
                System.out.println(xml.getMachineId());
                if ( md.containsKey(xml.getMachineId())) {
                     
                      MachineData mdt = md.get(xml.getMachineId());
                    for (XmlMetrologyWafer wf : xml.getWafers()) {
                        pl = new ArrayList();
                        if (mdt!=null && mdt.getProfileData().containsKey(wf.getMeasurements().get(0).getTargetLabel())) {
                            ProfileData pdt = mdt.getProfileData().get(wf.getMeasurements().get(0).getTargetLabel());
                            
                            for (int i = 0; i < wf.getMeasurements().size(); i++) {
                                if (pdt.getPlotData().get(i).getTargetPositionX() == wf.getMeasurements().get(i).getTargetPosition().getX()
                                        && pdt.getPlotData().get(i).getTargetPositionY() == wf.getMeasurements().get(i).getTargetPosition().getY()
                                        && pdt.getPlotData().get(i).getFieldPositionX() == wf.getMeasurements().get(i).getFieldPosition().getX()
                                        && pdt.getPlotData().get(i).getFieldPositionY() == wf.getMeasurements().get(i).getFieldPosition().getY()
                                        && pdt.getPlotData().get(i).getAperture().equals(wf.getMeasurements().get(i).getTargetRotation().toString())) {
                                    pl.add(new PlotData(wf.getMeasurements().get(i).getTargetRotation(), wf.getMeasurements().get(i).getFieldPosition().getX(), wf.getMeasurements().get(i).getFieldPosition().getY(), wf.getMeasurements().get(i).getTargetPosition().getX(), wf.getMeasurements().get(i).getTargetPosition().getY(), (pdt.getNoComponentFiles()*pdt.getPlotData().get(i).getOverlayX()+ wf.getMeasurements().get(i).getOverlay().getX())/ (pdt.getNoComponentFiles()+1),(pdt.getNoComponentFiles()*pdt.getPlotData().get(i).getOverlayY()+ wf.getMeasurements().get(i).getOverlay().getY())/ (pdt.getNoComponentFiles()+1)));
                                } else {
                                    pl.add(new PlotData(wf.getMeasurements().get(i).getTargetRotation(), wf.getMeasurements().get(i).getFieldPosition().getX(), wf.getMeasurements().get(i).getFieldPosition().getY(), wf.getMeasurements().get(i).getTargetPosition().getX(), wf.getMeasurements().get(i).getTargetPosition().getY(), wf.getMeasurements().get(i).getOverlay().getX(), wf.getMeasurements().get(i).getOverlay().getY()));
                                }
                            }
                            pdt.setPlotData(pl);
                            pd.put(pdt.getProfileName(),pdt);
                            
                        } else {
                            for (XmlMeasurement ms : wf.getMeasurements()) {
                                pl.add(new PlotData(ms.getTargetRotation(), ms.getFieldPosition().getX(), ms.getFieldPosition().getY(), ms.getTargetPosition().getX(), ms.getTargetPosition().getY(), ms.getOverlay().getX(), ms.getOverlay().getY()));
                            }
                            pd.put(wf.getMeasurements().get(0).getTargetLabel(), new ProfileData(1, wf.getMeasurements().get(0).getTargetLabel(), pl));
                            
                        }
                       
                    }
                     mdt.setProfileData(pd);
                        md.put(mdt.getMachineName(),mdt);
                } else {
                    for (XmlMetrologyWafer wf : xml.getWafers()) {
                        for (XmlMeasurement ms : wf.getMeasurements()) {
                            pl.add(new PlotData(ms.getTargetRotation(), ms.getFieldPosition().getX(), ms.getFieldPosition().getY(), ms.getTargetPosition().getX(), ms.getTargetPosition().getY(), ms.getOverlay().getX(), ms.getOverlay().getY()));
                        }
                        pd.put(wf.getMeasurements().get(0).getTargetLabel(), new ProfileData(1, wf.getMeasurements().get(0).getTargetLabel(), pl));
                        pl = new ArrayList();
                    }
                }
                md.put(xml.getMachineId(), new MachineData(xml.getMachineId(), pd));
                pd = new HashMap<>();
            } catch (final XMLException | IOException e) {
                throw new IllegalStateException(String.format("Cannot read '%s' for reading.", file.toString()));
            }
            
        }
        return md;
    }
    
}
