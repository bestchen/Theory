package cn.it.com.theroy.iview;

import cn.it.com.theroy.activity.ChapterActivity;


public interface IChapterView {

    ChapterActivity getActivity();

    void showTipView(int position);
}
