/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.service.impl;

import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.service.api.DataProvider;
import static com.ro.ssc.app.client.utils.ExcelReader.readExcel;
import static com.ro.ssc.app.client.utils.AccessReader.updateUserMap;
import java.io.File;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */


public enum DataProviderImpl implements DataProvider {

    
    
    
     INSTANCE {
         private Map<String, User> userData;
    
     

         @Override
    public Map<String, User> getUserData() {
        return userData;
    }

    @Override
    public void importUserData(File file) {
        userData = readExcel(file);
    }

  

         @Override
         public void enrichUserData(File file) {
         userData=updateUserMap(userData,file); 
         }

        
     
            };
     
     public static DataProvider getInstance() {
    return DataProviderImpl.INSTANCE;
}
   

}

