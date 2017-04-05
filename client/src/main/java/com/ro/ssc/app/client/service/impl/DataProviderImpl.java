/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.service.impl;

import com.ro.ssc.app.client.controller.content.sumary.SumaryController;
import com.ro.ssc.app.client.model.commons.DailyData;
import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.model.commons.ShiftData;
import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.service.api.DataProvider;
import static com.ro.ssc.app.client.utils.AccessReader.getShiftData;
import static com.ro.ssc.app.client.utils.AccessReader.updateUserMap;
import static com.ro.ssc.app.client.utils.ExcelReader.readExcel;
import static com.ro.ssc.app.client.utils.Utils.formatMillis;
import static com.ro.ssc.app.client.utils.Utils.formatMillis2;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public enum DataProviderImpl implements DataProvider {

    INSTANCE {
                private String MDB_PATH = "mdb";
                private Map<String, User> userData = new HashMap();
                private Map<String, Map<String, ShiftData>> shiftData;
                private Set<String> excludedGates;
                private Set<String> excludedUsers;
                private DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");
                private DateTimeFormatter dtf2 = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
                 private DateTimeFormatter dtf3 = DateTimeFormat.forPattern("dd-MM-yyyy");
                private LocalTime time;
                private DecimalFormat df = new DecimalFormat();
                private final Logger log = LoggerFactory.getLogger(DataProviderImpl.class);

                @Override
                public List<GenericModel> getUserData() {
                    List<GenericModel> data = new ArrayList<>();
                    for (Map.Entry<String, User> entry : userData.entrySet()) {
                        for (Event ev : entry.getValue().getEvents()) {
                            try {
                                if (entry.getValue().getName().contains("*")) {

                                    data.add(new GenericModel(ev.getEventDateTime().toString(dtf2), ev.getEventDateTime().toString(dtf), entry.getValue().getName().toUpperCase(), df.parse(entry.getValue().getCardNo()), entry.getValue().getDepartment(), ev.getAddr().contains("In") ? "Intrare" : "Iesire"));
                                } else {

                                    data.add(new GenericModel(ev.getEventDateTime().toString(dtf2), ev.getEventDateTime().toString(dtf), entry.getValue().getName(), df.parse(entry.getValue().getCardNo()), entry.getValue().getDepartment(), ev.getAddr().contains("In") ? "Intrare" : "Iesire"));

                                }
                            } catch (ParseException ex) {
                                java.util.logging.Logger.getLogger(SumaryController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                    data.sort((GenericModel o1, GenericModel o2) -> o1.getOne().toString().compareTo(o2.getOne().toString()));
                    return data;
                }

                @Override
                public List<GenericModel> getOverallTableData(DateTime iniDate, DateTime endDate, String department) {

                    List<GenericModel> data = new ArrayList<>();
                    for (Map.Entry<String, User> entry : userData.entrySet()) {
                        if (!excludedUsers.contains(entry.getValue().getName())) {
                            if (department == null || (entry.getValue().getDepartment().equals(department))) {

                                Long tduration = 0L;
                                Long tpause = 0L;
                                Long tovertime = 0L;
                                Long tundertime = 0l;
                                int tabsent = 0;
                                int tlaters = 0;
                                long tlate = 0;
                                int tearlys = 0;
                                long tearly = 0;
                                boolean withWrongEv = false;
                                List<DailyData> dailyList = DataProviderImplHelper.getListPerDay(userData, time, shiftData, excludedGates, entry.getKey(), iniDate, endDate);
                                for (DailyData day : dailyList) {

                                    if (day.getLateTime() > 0) {
                                        tlaters++;
                                    }
                                    tlate += day.getLateTime();
                                    if (day.getEarlyTime() > 0) {
                                        tearlys++;
                                    }
                                    tearly += day.getEarlyTime();
                                    if (day.getFirstInEvent().equals("") || day.getLastOutEvent().equals("")) {
                                        if (day.getWrongEvents().size() > 0) {
                                            withWrongEv = true;
                                        }

                                        tabsent++;

                                    }
                                    tduration += day.getWorkTime();
                                    tpause += day.getPauseTime();
                                    if (day.getOverTime() > 0) {
                                        tovertime += day.getOverTime();
                                    } else {
                                        tundertime += Math.abs(day.getOverTime());
                                    }
                                }

                                data.add(new GenericModel(entry.getValue().getName().toUpperCase(), entry.getValue().getDepartment(), formatMillis(tduration), formatMillis(tpause), formatMillis(tpause + tduration), formatMillis(tovertime), withWrongEv == true ? tabsent + "***" : tabsent + "", formatMillis(tlate) + "(" + tlaters + ")", formatMillis(tearly) + "(" + tearlys + ")", formatMillis(tundertime), formatMillis(tovertime - tundertime)));
                            }
                        }

                    }

                    data.sort((GenericModel o1, GenericModel o2) -> o1.getOne().toString().compareTo(o2.getOne().toString()));
                    return data;
                }

                @Override
                public List<GenericModel> getUserSpecificTableData(String user, DateTime iniDate, DateTime endDate) {

                    List<GenericModel> data = new ArrayList<>();

                    if (user != null && !excludedUsers.contains(user)) {

                        List<DailyData> dailyList = DataProviderImplHelper.getListPerDay(userData, time, shiftData, excludedGates, user, iniDate, endDate);
                        for (DailyData day : dailyList) {

                            int absent = 0;

                            if (day.getFirstInEvent().equals("") || day.getLastOutEvent().equals("")) {
                                if (day.getWrongEvents().size() > 0) {
                                    absent = 2;
                                } else {
                                    absent = 1;
                                }
                            }
                            data.add(new GenericModel(day.getDate().toString(dtf2), day.getFirstInEvent(), day.getLastOutEvent(), formatMillis(day.getWorkTime()), formatMillis(day.getPauseTime()), formatMillis(day.getWorkTime() + day.getPauseTime()), formatMillis(day.getOverTime()), absent == 2 ? "Da***" : absent == 1 ? "Da" : "", formatMillis(day.getLateTime()), formatMillis(day.getEarlyTime())));
                        }
                    }

                    return data;
                }

        @Override
        public List<GenericModel> getDaySpecificTableData(String department, DateTime iniDate) {

            List<GenericModel> data = new ArrayList<>();
            for (Map.Entry<String, User> entry : userData.entrySet()) {
                if(!excludedUsers.contains(entry.getKey())){
                    if( department == null || entry.getValue().getDepartment().equals(department)){
                       
                                  List<DailyData> dd= DataProviderImplHelper.getListOfDay(entry.getKey(), userData,iniDate,time,  excludedGates);

                    for(DailyData d:dd) {

                         data.add(new GenericModel(entry.getValue().getName(),d.getFirstInEvent(),d.getAdditionalDetails(),d.getLastOutEvent(),formatMillis2(d.getWorkTime()),formatMillis2(d.getPauseTime()),formatMillis2(d.getWorkTime()+d.getPauseTime()),entry.getValue().getDepartment(),formatMillis2(d.getWorkTime())));
                    }
                }
                }
                
            }
            
            
             List<GenericModel> result = new ArrayList<>();
             Map<String,List<GenericModel>> tm= new TreeMap();
             tm.putAll(data.stream().collect(Collectors.groupingBy(o->getDepartmentFromUser(getKeyFromUser(o.getOne().toString())))));
             for(Map.Entry<String,List<GenericModel>>d:tm.entrySet())
             {
                 
                  List<GenericModel> r = new ArrayList<>();
                  r=d.getValue();
                  r.sort((o1,o2)->o1.getOne().toString().compareTo(o2.getOne().toString()));
             result.addAll(r);
             }
               return result;
        }

        
        private String getKeyFromUser(String user){
         List<Map.Entry<String,User>> l=  userData.entrySet().stream().filter(p->p.getValue().getName().equals(user)).collect(Collectors.toList());
            return l.get(0).getKey();
    
        }
        
        @Override
                public DateTime getPossibleDateEnd(String user) {
                    DateTime result = new DateTime().withYear(1970);

                    if (user.equals("all")) {
                        for (Map.Entry<String, User> entry : userData.entrySet()) {
                            for (Event ev : entry.getValue().getEvents()) {
                                if (ev.getEventDateTime().isAfter(result)) {
                                    result = ev.getEventDateTime();
                                }
                            }
                        }
                    } else if (userData.containsKey(user)) {
                        for (Event ev : userData.get(user).getEvents()) {
                            if (ev.getEventDateTime().isAfter(result)) {
                                result = ev.getEventDateTime();
                            }
                        }
                    }

                    return result;
                }

                @Override
                public DateTime getPossibleDateStart(String user) {
                    DateTime result = DateTime.now();

                    if (user.equals("all")) {
                        for (Map.Entry<String, User> entry : userData.entrySet()) {
                            for (Event ev : entry.getValue().getEvents()) {
                                if (ev.getEventDateTime().isBefore(result)) {
                                    result = ev.getEventDateTime();
                                }
                            }
                        }
                    } else if (userData.containsKey(user)) {
                        for (Event ev : userData.get(user).getEvents()) {
                            if (ev.getEventDateTime().isBefore(result)) {
                                result = ev.getEventDateTime();
                            }
                        }
                    }
                    return result;
                }

                @Override
                public List<String> getUsers() {
                    List<String> result = new ArrayList<>(userData.keySet());
                    result.sort((String o1, String o2) -> o1.compareTo(o2));
                    return result;
                }

                @Override
                public List<String> getDepartments() {
                    Set<String> result = new LinkedHashSet<>();

                    for (Map.Entry<String, User> entry : userData.entrySet()) {
                        result.add(entry.getValue().getDepartment());
                    }
                    List<String> res = new ArrayList<>(result);
                    res.sort((String o1, String o2) -> o1.compareTo(o2));
                    return res;
                }

                @Override
                public void saveMdbFile(File srcFile) {
                    File destDir = new File(MDB_PATH);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                    }

                    try {
                        FileUtils.cleanDirectory(destDir);
                        FileUtils.copyFileToDirectory(srcFile, destDir);
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(DataProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                @Override
                public void importUserData(File file) {
                    userData = readExcel(file);

                    enrichUserData();
                }

                public LocalTime getTime() {
                    return time;
                }

                public void setTime(LocalTime time) {
                    this.time = time;
                }

                private void enrichUserData() {

                    File dir = new File(MDB_PATH);
                    if (dir.exists()) {
                        updateUserMap(dir.listFiles()[0]).get(0).stream().forEach(p -> {
                            String userId = p.split("#")[1];
                            String userName = p.split("#")[0];
                            if (userData.containsKey(p)) {
                                userData.get(p).setUserId(userId);
                            }
                        });
                        excludedGates = updateUserMap(dir.listFiles()[0]).get(1);
                        excludedUsers = updateUserMap(dir.listFiles()[0]).get(2);
                        shiftData = getShiftData(dir.listFiles()[0]);
                    }
                }

                @Override
                public String getCellData(String u, DateTime ini, DateTime end, int ordinal) {
                   
                    
                                Long tduration = 0L;
                                Long tpause = 0L;
                                Long tovertime = 0L;
                                Long tundertime = 0l;
                                int tabsent = 0;
                                int tlaters = 0;
                                long tlate = 0;
                                int tearlys = 0;
                                long tearly = 0;
                                boolean withWrongEv = false;
                            
                    List<DailyData> dailyList = DataProviderImplHelper.getListPerDay(userData, time, shiftData, excludedGates, u, ini,end);
                         for (DailyData day : dailyList) {

                                    if (day.getLateTime() > 0) {
                                        tlaters++;
                                    }
                                    tlate += day.getLateTime();
                                    if (day.getEarlyTime() > 0) {
                                        tearlys++;
                                    }
                                    tearly += day.getEarlyTime();
                                    if (day.getFirstInEvent().equals("") || day.getLastOutEvent().equals("")) {
                                        if (day.getWrongEvents().size() > 0) {
                                            withWrongEv = true;
                                        }

                                        tabsent++;

                                    }
                                    if(DataImportImpl.getInstance().hasDayUserDepartment(u.split("#")[0], userData.get(u).getDepartment(), day.getDate()))
                                    {
                                    final DailyData da=DataImportImpl.getInstance().getWorkData(u.split("#")[0],  day.getDate());
                                     tduration += da.getWorkTime();
                                       tpause += da.getPauseTime();
                                    }else{
                                    tduration += day.getWorkTime();
                                       tpause += day.getPauseTime();
                                    }
                                 
                                    if (day.getOverTime() > 0) {
                                        tovertime += day.getOverTime();
                                    } else {
                                        tundertime += Math.abs(day.getOverTime());
                                    }
                                }

                               
                    if(!ini.equals(end)){
                    if (ordinal==1) {
                        return formatMillis2(tduration+tpause);
                    } else if (ordinal==2) {
                        return formatMillis2(tpause);

                    } else if (ordinal==3) {     
                        return formatMillis2(tduration);

                    }
                    
                    } else{
                        dailyList= DataProviderImplHelper.getListPerDay(userData, time, shiftData, excludedGates, u, ini.minusDays(1),end.plusDays(1));
                        if (!dailyList.isEmpty()) {
                            if(DataImportImpl.getInstance().hasDayUserDepartment(u.split("#")[0], userData.get(u).getDepartment(), ini.withTimeAtStartOfDay()))
                            {
                                long wt=DataImportImpl.getInstance().getWorkData(u.split("#")[0],  ini.withTimeAtStartOfDay()).getWorkTime();
                                if(wt==-1){
                                return "?";
                                }
                            return formatMillis2(wt);
                            }
                                for(DailyData dd:dailyList){
                                   // log.debug(u+" "+ini+" "+dd.toString());
                                    if(dd.getDate().withTimeAtStartOfDay().equals(ini.withTimeAtStartOfDay())){
                                        String ot=dd.getOverTime()==0?"": "\n"+formatMillis2(dd.getOverTime());
                                return formatMillis2(dd.getWorkTime()) +""+ot  ;
                                    }
                                }
                        }
                    }

                    return "";
                }

                @Override
                public String getDepartmentFromUser(String entry) {

                    if (userData.containsKey(entry)) {
                        return userData.get(entry).getDepartment();
                    }
                    return "";
                }

                @Override
                public List<String> getUsersDep(String department) {


                    List<String> result = new ArrayList<>();
 Map<String,List<User>> tm= new TreeMap();
           tm.putAll(userData.values().stream().collect(Collectors.groupingBy(u->u.getDepartment())));
                    for (Map.Entry<String,List<User>> entry :tm.entrySet()) {

                        if (department.equals("all") || department.equals(entry.getKey())) {
                            final List<User> userList= entry.getValue();
                            userList.sort((Comparator.comparing(User::getName)));
                            for(User user: userList){
                                result.add(user.getName()+"#"+user.getUserId());
                            }
                        }
                    }
                    return result;
                }

            };

    public static DataProviderImpl getInstance() {
        return DataProviderImpl.INSTANCE;
    }

    public void setTime(LocalTime lt) {
        getInstance().setTime(lt);
    }
    
    
    

}
