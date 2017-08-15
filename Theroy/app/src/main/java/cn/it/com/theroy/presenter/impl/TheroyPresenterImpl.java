package cn.it.com.theroy.presenter.impl;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cn.it.com.theroy.R;
import cn.it.com.theroy.activity.ChapterActivity;
import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.download.DownLoadListener;
import cn.it.com.theroy.download.DownLoadManager;
import cn.it.com.theroy.download.DownLoadService;
import cn.it.com.theroy.download.TaskInfo;
import cn.it.com.theroy.download.db.SQLDownLoadInfo;
import cn.it.com.theroy.iview.ITheoryView;
import cn.it.com.theroy.mamager.MainThreadExecutor;
import cn.it.com.theroy.mamager.ThreadPool;
import cn.it.com.theroy.presenter.ITheroyPresenter;
import cn.it.com.theroy.uitls.FileUtils;
import cn.it.com.theroy.uitls.PreferencesUtils;
import cn.it.com.theroy.widget.ItemBookView;
import cn.it.com.theroy.widget.TheroyDialog;
import cn.it.com.theroy.widget.TheroyDialogContentView;

import static cn.it.com.theroy.uitls.UIUitls.getResources;

public class TheroyPresenterImpl implements ITheroyPresenter {

    private WeakReference<ITheoryView> iView;
    /*使用DownLoadManager时只能通过DownLoadService.getDownLoadManager()的方式来获取下载管理器，不能通过new DownLoadManager()的方式创建下载管理器*/
    private DownLoadManager downLoadManager;
    private ArrayList<TaskInfo> downloadingTaskInfos = new ArrayList<TaskInfo>();

    public TheroyPresenterImpl(ITheoryView iView) {
        this.iView = new WeakReference<ITheoryView>(iView);
        handler.sendEmptyMessageDelayed(0, 100);
    }

    @Override
    public void init() {
        if (iViewNull()) {
            return;
        }
    }

    @Override
    public void onLoadAssetsRes() {
        ThreadPool.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                final boolean sucess = FileUtils.copyAssets("", FileUtils.getDirPath());
                MainThreadExecutor.post(getTask(), new Runnable() {
                    @Override
                    public void run() {
                        if (sucess) {
                            iView.get().onLoadAssetsResSucess();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void initRecordItems() {
        String[] recordIds = getResources().getStringArray(R.array.theory_record_id);
        final String[] chapters = getResources().getStringArray(R.array.theory_record_chapter);
        ArrayList<TheoryItemBean> records = new ArrayList<>(recordIds.length);
        for (int i = 0, len = recordIds.length; i < len; i++) {
            final String downloadTips = FileUtils.checkDownloaded(recordIds[i]);
            TheoryItemBean readTheoryItem = TheoryItemBean.createRecodItem(recordIds[i], downloadTips, chapters[i], TheoryItemBean.TAG_RECORD, i + 1);
            records.add(readTheoryItem);
        }
        iView.get().initRecord(records);
    }

    @Override
    public void initOriginItems() {
        String[] originIds = getResources().getStringArray(R.array.theory_origin_id);
        ArrayList<TheoryItemBean> origins = new ArrayList<>(originIds.length);
        final String[] chapters = getResources().getStringArray(R.array.theory_origin_chapter);
        final int len = Math.min(originIds.length, chapters.length);
        for (int i = 0; i < len; i++) {
            TheoryItemBean item = TheoryItemBean.createOriginItem(originIds[i], chapters[i], TheoryItemBean.TAG_ORIGIN, i + 1);
            origins.add(item);
        }
        iView.get().initOrigin(origins);
    }

    @Override
    public void openLastChapter(final String tag, final int index) {
        if (!TextUtils.isEmpty(tag) && index != -1) {

            final TheroyDialog dialog = new TheroyDialog(iView.get().getActivity());
            dialog.setTag(new TheoryItemBean());
            dialog.setContentView();
            String title;
            if (TextUtils.equals(TheoryItemBean.TAG_RECORD, tag)) {
                final String[] chapters = getResources().getStringArray(R.array.theory_record_chapter);
                title = "您上次看到 菩提到次第广论讲记" + chapters[index - 1] + PreferencesUtils.getString(PreferencesUtils.SPKeys.DETAIL_TXT_TITLE, "");
            } else {
                final String[] chapters = getResources().getStringArray(R.array.theory_origin_chapter);
                title = "您上次看到 菩提到次第广论原文" + chapters[index - 1] + PreferencesUtils.getString(PreferencesUtils.SPKeys.DETAIL_TXT_TITLE, "");
            }
            dialog.setTitle(title);
            dialog.setDesc("是否要自动跳转");
            dialog.setCancelText("取消");
            dialog.setConfirmText("确认");
            dialog.setOnDialogClickListener(new TheroyDialogContentView.OnDialogClickListener() {
                @Override
                public void onCancel() {
                }

                @Override
                public void onConfirm(TheoryItemBean bean) {
                    startChapterActivity(tag, index, false);
                }
            });
            dialog.show();
        }
    }

    @Override
    public void downLoad(TheoryItemBean bean) {
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
                    downLoadManager = DownLoadService.getDownLoadManager();
                    return;
                }
                downLoadManager.addTask(bean.getId(), bean.getDownloadPath(), bean.getId());
                downloadingTaskInfos.add(info);
                downLoadManager.setAllTaskListener(new DownLoadListener() {
                    @Override
                    public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {
                        if (!iViewNull()) {
                            iView.get().onStartDownload(sqlDownLoadInfo);
                        }
                    }

                    @Override
                    public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
                        if (!iViewNull()) {
                            iView.get().onProgressDownload(sqlDownLoadInfo, isSupportBreakpoint);
                        }
                    }

                    @Override
                    public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
                        if (!iViewNull()) {
                            iView.get().onStopDownload(sqlDownLoadInfo, isSupportBreakpoint);
                        }
                    }

                    @Override
                    public void onError(SQLDownLoadInfo sqlDownLoadInfo) {
                        if (!iViewNull()) {
                            iView.get().onErrorDownload(sqlDownLoadInfo);
                        }
                    }

                    @Override
                    public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
                        if (!iViewNull()) {
                            iView.get().onSucessDownload(sqlDownLoadInfo);
                        }

                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    public void openTheory(ItemBookView itemBookView, TheoryItemBean bean) {
        if (!iViewNull()) {
            final int index = bean.getIndex();
            final String tag = bean.getTag();
            if (PreferencesUtils.getInt(PreferencesUtils.SPKeys.CHAPTER_INDEX, -1) != index) {
                PreferencesUtils.putInt(PreferencesUtils.SPKeys.DETAIL_POSITION, -1);
            }
            PreferencesUtils.putInt(PreferencesUtils.SPKeys.CHAPTER_INDEX, index);
            PreferencesUtils.putString(PreferencesUtils.SPKeys.CHAPTER_TAG, tag);

            startChapterActivity(tag, index, true);
        }
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
    public void saveTheoryItem(String path, TheoryItemBean bean) {
        if (!TextUtils.isEmpty(path) && bean != null) {
            FileUtils.saveTheoryItem(path, bean);
        }
    }

    @Override
    public void onDestroy() {
        downLoadManager.stopAllTask();
        MainThreadExecutor.cancelAllRunnables(getTask());
    }

    private boolean iViewNull() {
        return iView == null || iView.get() == null;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            //获取下载管理器
            downLoadManager = DownLoadService.getDownLoadManager();
            if (downLoadManager != null) {
                downLoadManager.changeUser("theory");
                downloadingTaskInfos = downLoadManager.getAllTask();
                downLoadManager.setSupportBreakpoint(true);
            }
            recoverDownload();
        }
    };

    private void startChapterActivity(String tag, int index, boolean isClick) {
        if (iViewNull()) {
            return;
        }
        Intent intent = new Intent(iView.get().getActivity(), ChapterActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("tag", tag);
        intent.putExtra("click", isClick);
        iView.get().getActivity().startActivity(intent);
    }

    private void recoverDownload() {
        if (downLoadManager == null) {
            downLoadManager = DownLoadService.getDownLoadManager();
            return;
        }
        ArrayList<TaskInfo> allTask = downLoadManager.getAllTask();
        downloadingTaskInfos.addAll(allTask);
        downLoadManager.startAllTask();
        downLoadManager.setAllTaskListener(new DownLoadListener() {
            @Override
            public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {
                if (!iViewNull()) {
                    iView.get().onStartDownload(sqlDownLoadInfo);
                }
            }

            @Override
            public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
                if (!iViewNull()) {
                    iView.get().onProgressDownload(sqlDownLoadInfo, isSupportBreakpoint);
                }
            }

            @Override
            public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
                if (!iViewNull()) {
                    iView.get().onStopDownload(sqlDownLoadInfo, isSupportBreakpoint);
                }
            }

            @Override
            public void onError(SQLDownLoadInfo sqlDownLoadInfo) {
                if (!iViewNull()) {
                    iView.get().onErrorDownload(sqlDownLoadInfo);
                }
            }

            @Override
            public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
                if (!iViewNull()) {
                    iView.get().onSucessDownload(sqlDownLoadInfo);
                }

            }
        });
    }

    private Object getTask() {
        return this.getClass().hashCode();
    }
}
