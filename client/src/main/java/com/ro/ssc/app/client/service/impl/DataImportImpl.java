package com.ro.ssc.app.client.service.impl;

import com.ro.ssc.app.client.model.commons.DailyData;
import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.service.api.DataImport;
import com.ro.ssc.app.client.utils.ExcelReader;
import static com.ro.ssc.app.client.utils.Utils.formatMillis2;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;

/**
 * Created by MCorciova on 3/9/2017.
 */
public enum DataImportImpl implements DataImport {

    INSTANCE {
                private final List<DailyData> dd = new ArrayList();

                @Override
                public void importData(File dir) {
                    dd.clear();
                    for (File file : dir.listFiles()) {
                        dd.addAll(ExcelReader.readFile(file));
                        
                    }
                    dd.forEach(d->{
                            if(d.getAdditionalDetails().contains("Vaho"))
                                    {
                                        System.out.println(d.toString());
                                            
                                            };
                    });
                }

                @Override
                public DailyData getWorkData(String u, String department, DateTime date) {
                  if(department.contains("Vaho"))
                        {
                      final List<DailyData> dai=  dd.stream().filter(d -> d.getUserId().equals(u) && d.getAdditionalDetails().equals(department) && d.getDate().withTimeAtStartOfDay().equals(date.withTimeAtStartOfDay())).collect(Collectors.toList());    
                  //  System.out.println(u+" "+date+" "+dai.size());
                    if(dai.size()>1)
                    {
                                        System.out.println(u+" "+date+" "+dai.get(0));
                                   return dd.stream().filter(d -> d.getUserId().equals(u) && d.getAdditionalDetails().equals(department)&& d.getDate().withTimeAtStartOfDay().equals(date.withTimeAtStartOfDay())).collect(Collectors.toList()).get(1);


                    }
                        }
                    return dd.stream().filter(d -> d.getUserId().equals(u) && d.getAdditionalDetails().equals(department)&& d.getDate().withTimeAtStartOfDay().equals(date.withTimeAtStartOfDay())).collect(Collectors.toList()).get(0);
                }

                @Override
                public boolean hasDayUserDepartment(String user, String department, DateTime date) {
                                     

                    if (dd.isEmpty()) {
                        return false;
                    } else {
                        if(department.contains("Vaho"))
                        {
                           System.out.println(user+" "+date+" "+ dd.stream().filter(d -> d.getUserId().equals(user) && d.getAdditionalDetails().equals(department) && d.getDate().withTimeAtStartOfDay().equals(date.withTimeAtStartOfDay())).collect(Collectors.toList()).size());
                 
                        }
                        return dd.stream().anyMatch(d -> d.getUserId().equals(user) && d.getDate().withTimeAtStartOfDay().equals(date.withTimeAtStartOfDay()) && d.getAdditionalDetails().equals(department));
                    }
                }
            };

    public static DataImportImpl getInstance() {
        return DataImportImpl.INSTANCE;
    }

}
