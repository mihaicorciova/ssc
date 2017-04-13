/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.service.api;

import com.ro.ssc.app.client.model.commons.GenericModel;
import java.io.File;
import java.time.LocalTime;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author DauBufu
 */
public interface DataProvider {

    public List<GenericModel> getUserData();

    public void importUserData(File file);

    public List<GenericModel> getOverallTableData(DateTime iniDate, DateTime endDate, String department);

    public List<GenericModel> getUserSpecificTableData(String user, DateTime iniDate, DateTime endDate);


    public List<GenericModel> getDaySpecificTableData(String department, DateTime iniDate);

    public DateTime getPossibleDateStart(String userId);

    public DateTime getPossibleDateEnd(String userId);

    public List<String> getUsers();
    
    public String getKeyFromUser(String user);
    
    public List<String> getUsersDep(String department);

    public List<String> getDepartments();

    public void saveMdbFile(File file);

    public String getCellData(String u, DateTime ini, DateTime end, int ordinal);

    public String getDepartmentFromUser(String entry);
    
    
}
