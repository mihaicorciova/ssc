/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asml.wfa.metrotools.tooltotoolmatching.api;

import com.asml.wfa.xml.adel.adelmetrology.AdelMetrology;
import java.io.File;
import java.util.List;

/**
 *
 * @author Mihai Corciova <mihai.corciova@asml.com>
 */
public interface MultiYSMatichingApi {
    
    public List<AdelMetrology> parseFiles(List<File> files);
    
}
