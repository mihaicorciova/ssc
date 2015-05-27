package com.asml.lis.client.service.metrologysetup;

import com.asml.lis.client.service.metrologysetup.model.MachineData;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mihai Corciova <mihai.corciova@asml.com>
 */
public interface MultiYSQualificationService {

    /**
     * @param files
     * @return
     */
    Map<String,MachineData> parseMetrologyFiles(List<File> files);

}
