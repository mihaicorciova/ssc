/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asml.wfa.metrotools.tooltotoolmatching.impl;

import com.asml.wfa.metrotools.tooltotoolmatching.api.MultiYSMatichingApi;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.adel.AdelMetroDocumentReader;
import com.asml.wfa.xml.adel.adelmetrology.AdelMetrology;
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
public class MultiYSMatichingApiImpl implements MultiYSMatichingApi {

    @Override
    public List<AdelMetrology> parseFiles(List<File> files) {
        List<AdelMetrology> ls = new ArrayList<AdelMetrology>();
        for(File file:files){
        try (final InputStream stream = Files.newInputStream(file.toPath())) {
               
                
                ls.add(AdelMetroDocumentReader.loadAdelMetrology(stream));
              

            } catch (final XMLException | IOException e) {
                throw new IllegalStateException(String.format("Cannot read '%s' for reading.", file.toString()));
            }
    }
        return ls;
    }
}
