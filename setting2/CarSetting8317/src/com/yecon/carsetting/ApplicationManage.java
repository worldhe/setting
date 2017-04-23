package com.yecon.carsetting;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

import com.yecon.carsetting.DateBase.MapListDB;

public class ApplicationManage extends Application {
    public static ApplicationManage mInstance;
    private List<Activity> activityList = new LinkedList<Activity>();
    private MapListDB mMapListDB;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();
    }

    public static ApplicationManage getInstance() {
        if (null == mInstance) {
            mInstance = new ApplicationManage();
        }
        return mInstance;
    }

    private void init() {
        mMapListDB = new MapListDB(this);
    }

    public synchronized MapListDB getMapListDB() {
        if (mMapListDB == null)
            mMapListDB = new MapListDB(this);
        return mMapListDB;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        //System.exit(0);
    }
}
