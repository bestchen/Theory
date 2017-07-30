package cn.it.com.theroy.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import cn.it.com.theroy.uitls.ConstUitls;
import cn.it.com.theroy.view.TheroyApplication;

/**
 * 类功能描述：下载器后台服务</br>
 */

public class DownLoadService extends Service {

    private static DownLoadManager downLoadManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public static DownLoadManager getDownLoadManager() {
        return downLoadManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downLoadManager = new DownLoadManager(DownLoadService.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放downLoadManager
        downLoadManager.stopAllTask();
        downLoadManager = null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (downLoadManager == null) {
            downLoadManager = new DownLoadManager(DownLoadService.this);
        }
        Intent in = new Intent(ConstUitls.DOWNLOAD_SERVICE_CREATED);
        LocalBroadcastManager.getInstance(TheroyApplication.getContext()).sendBroadcast(in);
    }


}
