package com.ro.ssc.app.client.service.api;

import java.io.File;
import java.util.List;
import org.joda.time.DateTime;

/**
 * Created by MCorciova on 3/9/2017.
 */
public interface DataImport {

    public void importData(File dir);

    public DateTime getPossibleDateStart(String userId);

    public DateTime getPossibleDateEnd(String userId);

    public List<String> getUsers();

    public List<String> getUsersDep(String department);

    public List<String> getDepartments();

    public String getCellData(String u, DateTime ini, DateTime end, int ordinal);

    public String getDepartmentFromUser(String entry);
}
