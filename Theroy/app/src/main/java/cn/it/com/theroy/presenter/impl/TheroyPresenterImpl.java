package cn.it.com.theroy.presenter.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cn.it.com.theroy.R;
import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.download.DownLoadListener;
import cn.it.com.theroy.download.DownLoadManager;
import cn.it.com.theroy.download.DownLoadService;
import cn.it.com.theroy.download.TaskInfo;
import cn.it.com.theroy.download.db.SQLDownLoadInfo;
import cn.it.com.theroy.presenter.ITheroyPresenter;
import cn.it.com.theroy.uitls.ConstUitls;
import cn.it.com.theroy.uitls.FileUtils;
import cn.it.com.theroy.view.ITheoryView;
import cn.it.com.theroy.view.TheroyApplication;
import cn.it.com.theroy.widget.ItemBookView;
import cn.it.com.theroy.widget.TheroyDialog;
import cn.it.com.theroy.widget.TheroyDialogContentView;

import static cn.it.com.theroy.uitls.UIUitls.getResources;

/**
 * Created by Chenweiwei on 2017/7/29.
 */

public class TheroyPresenterImpl implements ITheroyPresenter {

    private WeakReference<ITheoryView> iView;
    /*使用DownLoadManager时只能通过DownLoadService.getDownLoadManager()的方式来获取下载管理器，不能通过new DownLoadManager()的方式创建下载管理器*/
    private DownLoadManager downLoadManager;
    private ArrayList<TaskInfo> downloadingTaskInfos = new ArrayList<TaskInfo>();

    public TheroyPresenterImpl(ITheoryView iView) {
        this.iView = new WeakReference<ITheoryView>(iView);
        //获取下载管理器
        downLoadManager = DownLoadService.getDownLoadManager();
        if (downLoadManager != null) {
            downLoadManager.changeUser("theory");
            downloadingTaskInfos = downLoadManager.getAllTask();
        }
    }

    @Override
    public void init() {
        if (iViewNull()) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstUitls.DOWNLOAD_SERVICE_CREATED);
        LocalBroadcastManager.getInstance(TheroyApplication.getContext()).registerReceiver(receiver, intentFilter);
        initRecordItems();
        initOriginItems();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //设置用户ID，客户端切换用户时可以显示相应用户的下载任务
            downLoadManager = DownLoadService.getDownLoadManager();
            downLoadManager.changeUser("theory");
            downloadingTaskInfos = downLoadManager.getAllTask();
        }
    };

    private void initRecordItems() {
        String[] recordIds = getResources().getStringArray(R.array.theory_record_id);
        ArrayList<TheoryItemBean> records = new ArrayList<>(recordIds.length);
        for (String id : recordIds) {
            TheoryItemBean readTheoryItem = FileUtils.readTheoryItem(FileUtils.getRecordPath(id));
            if (readTheoryItem == null) {
                break;
            }
            records.add(readTheoryItem);
        }
        if (records.size() < recordIds.length) {
            records.clear();
            final String[] chapters = getResources().getStringArray(R.array.theory_record_chapter);
            final int len = Math.min(recordIds.length, chapters.length);
            for (int i = 0; i < len; i++) {
                TheoryItemBean item = TheoryItemBean.createRecodItem(recordIds[i], chapters[i]);
                records.add(item);
                FileUtils.saveTheoryItem(FileUtils.getRecordPath(recordIds[i]), item);
            }
        }
        iView.get().initRecord(records);
    }

    private void initOriginItems() {
        String[] originIds = getResources().getStringArray(R.array.theory_origin_id);
        ArrayList<TheoryItemBean> origins = new ArrayList<>(originIds.length);
        for (String id : originIds) {
            TheoryItemBean readTheoryItem = FileUtils.readTheoryItem(FileUtils.getOriginPath(id));
            if (readTheoryItem == null) {
                break;
            }
            origins.add(readTheoryItem);
        }
        if (origins.size() < originIds.length) {
            origins.clear();
            final String[] chapters = getResources().getStringArray(R.array.theory_origin_chapter);
            final int len = Math.min(originIds.length, chapters.length);
            for (int i = 0; i < len; i++) {
                TheoryItemBean item = TheoryItemBean.createOriginItem(originIds[i], chapters[i]);
                origins.add(item);
                FileUtils.saveTheoryItem(FileUtils.getOriginPath(originIds[i]), item);
            }
        }
        iView.get().initOrigin(origins);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void downLoad(final ItemBookView itemBookView, TheoryItemBean bean) {
        if (iViewNull()) {
            return;
        }
        final TheroyDialog dialog = new TheroyDialog(iView.get().getActivity());
        dialog.setTag(bean);
        if (!bean.isDownload()) {
            dialog.setTitle("你确认下载该章音频文件吗?");
            dialog.setDesc("建议您在WIFI情况下下载");
            dialog.setCancelText("取消");
            dialog.setConfirmText("确认");
        }
        dialog.setOnDialogClickListener(new TheroyDialogContentView.OnDialogClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm(final TheoryItemBean bean) {
                TaskInfo info = new TaskInfo();
                info.setFileName(bean.getDownloadFileName());
                //服务器一般会有个区分不同文件的唯一ID，用以处理文件重名的情况
                info.setTaskID(bean.getDownloadFileName());
                info.setOnDownloading(true);
                //将任务添加到下载队列，下载器会自动开始下载
                if (downLoadManager == null) {
                    return;
                }
                downLoadManager.addTask(bean.getDownloadFileName(), bean.getDownloadPath(), bean.getDownloadFileName());
                downloadingTaskInfos.add(info);
                downLoadManager.setAllTaskListener(new DownLoadListener() {
                    @Override
                    public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {
                        if (!iViewNull()) {
                            iView.get().onStartDownload(itemBookView, sqlDownLoadInfo);
                        }
                    }

                    @Override
                    public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
                        if (!iViewNull()) {
                            iView.get().onProgressDownload(itemBookView, sqlDownLoadInfo, isSupportBreakpoint);
                        }
                    }

                    @Override
                    public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
                        if (!iViewNull()) {
                            iView.get().onStopDownload(itemBookView, sqlDownLoadInfo, isSupportBreakpoint);
                        }
                    }

                    @Override
                    public void onError(SQLDownLoadInfo sqlDownLoadInfo) {
                        if (!iViewNull()) {
                            iView.get().onErrorDownload(itemBookView, sqlDownLoadInfo);
                        }
                    }

                    @Override
                    public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
                        if (!iViewNull()) {
                            iView.get().onSucessDownload(itemBookView, sqlDownLoadInfo);
                        }

                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    public void openTheory(ItemBookView itemBookView, TheoryItemBean bean) {

    }

    @Override
    public ArrayList<TaskInfo> getDownloadingTaskInfos() {
        return downloadingTaskInfos;
    }

    @Override
    public TaskInfo getTaskInfo(String taskID) {
        return downLoadManager.getTaskInfo(taskID);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(TheroyApplication.getContext()).unregisterReceiver(receiver);
        downLoadManager.stopAllTask();
    }

    private boolean iViewNull() {
        return iView == null || iView.get() == null;
    }

}
