package com.ro.ssc.app.client.service.impl;

import com.ro.ssc.app.client.model.commons.DailyData;
import com.ro.ssc.app.client.service.api.DataImport;
import com.ro.ssc.app.client.utils.ExcelReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
                }

                @Override
                public DateTime getPossibleDateEnd(String userId) {
                    final List<DailyData> daily= dd;
                    daily.sort((o1,o2)->o1.getDate().isBefore(o2.getDate())==true?1:-1);
                    return daily.get(daily.size()-1).getDate();
                }

                @Override
                public List<String> getUsers() {
                    return new ArrayList(dd.stream().collect(Collectors.groupingBy(d -> d.getUserId())).keySet());
                }

                @Override
                public List<String> getUsersDep(String department) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public List<String> getDepartments() {
                    return new ArrayList(dd.stream().collect(Collectors.groupingBy(d -> d.getAdditionalDetails())).keySet());
                }

                @Override
                public String getCellData(String u, DateTime ini, DateTime end, int ordinal) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getDepartmentFromUser(String entry) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public DateTime getPossibleDateStart(String userId) {
                   final List<DailyData> daily= dd;
                    daily.sort((o1,o2)->o1.getDate().isBefore(o2.getDate())==true?1:-1);
                    return daily.get(0).getDate();
                }
            };

    public static DataImportImpl getInstance() {
        return DataImportImpl.INSTANCE;
    }

}
