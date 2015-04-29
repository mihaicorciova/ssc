/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asml.lis.client.service.metrologysetup.impl;

import com.asml.lis.client.service.metrologysetup.MultiYSQualificationService;
import com.asml.lis.client.service.metrologysetup.model.PlotData;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.adel.AdelMetroDocumentReader;
import com.asml.wfa.xml.adel.adelmetrology.AdelMetrology;
import com.asml.wfa.xml.adel.adelmetrology.XmlMeasurement;
import com.asml.wfa.xml.adel.adelmetrology.XmlMetrologyWafer;
import com.asml.wfa.xml.adel.jaxb.XMLException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mihai Corciova <mihai.corciova@asml.com>
 */
public class MultiYSQualificationServiceImpl implements MultiYSQualificationService {

    @Override
    public  List<PlotData> parseMetrologyFiles(File file) {

        List<PlotData> pl = new ArrayList<>();
         
        System.out.println(file.getName());
           
            try (final InputStream stream = Files.newInputStream(file.toPath())) {

                AdelMetrology xml = AdelMetroDocumentReader.loadAdelMetrology(stream);

                for (XmlMetrologyWafer wf : xml.getWafers()) {
                    for (XmlMeasurement ms : wf.getMeasurements()) {
                       pl.add(new PlotData(xml.getMachineId(),ms.getFieldPosition().getX(),ms.getFieldPosition().getY(),ms.getTargetPosition().getX(),ms.getTargetPosition().getY(),ms.getOverlay().getX(),ms.getOverlay().getY()));
                    }

                }
            } catch (final XMLException | IOException e) {
                throw new IllegalStateException(String.format("Cannot read '%s' for reading.", file.toString()));
            }
       
        return pl;
    }

}
