/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.service.api;

import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.model.commons.User;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;

/**
 *
 * @author DauBufu
 */
public interface DataProvider {

    public  List<GenericModel>  getUserData();

    public void importUserData(File file);
       
    public List<GenericModel> getOverallTableData (DateTime iniDate,DateTime endDate,String department);
      
    public List<GenericModel> getUserSpecificTableData (String user,DateTime iniDate,DateTime endDate);
    
    public DateTime getPossibleDateStart(String user);
    
     public DateTime getPossibleDateEnd(String user); 
     
     public List<String> getUsers();
     
     public List<String> getDepartments();
     
     public void saveMdbFile(File file);
}
