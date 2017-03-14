package com.ro.ssc.app.client.service.api;

import com.ro.ssc.app.client.model.commons.DailyData;
import java.io.File;
import java.util.List;
import org.joda.time.DateTime;

/**
 * Created by MCorciova on 3/9/2017.
 */
public interface DataImport {

    public void importData(File dir);

    public DailyData getWorkData(String u, DateTime date);

   
    public boolean hasDayUserDepartment(String user, String department, DateTime date);
}
