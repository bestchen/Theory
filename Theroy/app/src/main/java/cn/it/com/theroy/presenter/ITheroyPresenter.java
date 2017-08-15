package cn.it.com.theroy.presenter;

import java.util.ArrayList;

import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.download.TaskInfo;
import cn.it.com.theroy.widget.ItemBookView;


public interface ITheroyPresenter {

    void init();

    void onLoadAssetsRes();

    void initRecordItems();

    void initOriginItems();

    void openLastChapter(String tag,int index);

    void downLoad(TheoryItemBean bean);

    void openTheory(ItemBookView itemBookView, TheoryItemBean bean);

    ArrayList<TaskInfo> getDownloadingTaskInfos();

    TaskInfo getTaskInfo(String taskID);

    void saveTheoryItem(String path, TheoryItemBean bean);

    void onDestroy();

}
