/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.service.api;

import com.ro.ssc.app.client.model.commons.User;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author DauBufu
 */
public interface DataProvider {

    public Map<String, User> getUserData();

    public void importUserData(File file);

}
