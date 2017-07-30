package cn.it.com.theroy.presenter;

import java.util.ArrayList;

import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.download.TaskInfo;
import cn.it.com.theroy.widget.ItemBookView;

/**
 * Created by Chenweiwei on 2017/7/29.
 */

public interface ITheroyPresenter {

    void init();

    void onResume();

    void downLoad(ItemBookView itemBookView, TheoryItemBean bean);

    void openTheory(ItemBookView itemBookView, TheoryItemBean bean);

    ArrayList<TaskInfo> getDownloadingTaskInfos();

    TaskInfo getTaskInfo(String taskID);

    void onDestroy();

}
