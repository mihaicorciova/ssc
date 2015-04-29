package com.asml.lis.client.service.metrologysetup;

import com.asml.lis.client.service.metrologysetup.model.PlotData;
import java.io.File;
import java.util.List;

/**
 *
 * @author Mihai Corciova <mihai.corciova@asml.com>
 */
public interface MultiYSQualificationService {
   
    
     /**
      * @param file
      * @return 
      */
    List<PlotData> parseMetrologyFiles(File file);
    
}
