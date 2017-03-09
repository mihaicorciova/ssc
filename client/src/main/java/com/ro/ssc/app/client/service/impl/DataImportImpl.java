package com.ro.ssc.app.client.service.impl;

import com.ro.ssc.app.client.service.api.DataImport;

import java.io.File;

/**
 * Created by MCorciova on 3/9/2017.
 */
public enum DataImportImpl implements DataImport {

    INSTANCE {



        @Override
        public void importData(File dir) {

        }
    };


    public static DataImportImpl getInstance() {
        return DataImportImpl.INSTANCE;
    }

}
