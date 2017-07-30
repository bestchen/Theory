package cn.it.com.theroy.view;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import cn.it.com.theroy.download.DownLoadService;
import cn.it.com.theroy.uitls.UIUitls;

/**
 * Created by Chenweiwei on 2017/7/29.
 */

public class TheroyApplication extends Application {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        UIUitls.init(this);
        this.startService(new Intent(this, DownLoadService.class));
    }
}
