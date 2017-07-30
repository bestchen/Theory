package cn.it.com.theroy.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import cn.it.com.theroy.uitls.FileUtils;

/**
 * Created by Chenweiwei on 2017/7/30.
 */

public class TheoryItemBean implements Serializable {

    private String id = "";
    private boolean isDownload = false;
    private String downlaodText;
    private String chapterText;
    private String downloadPath;
    private String downloadFileName = id;

    private static TheoryItemBean creatBaseItem(String id) {
        TheoryItemBean item = new TheoryItemBean();
        item.setId(id);
        item.setDownlaodText("下载");
        return item;
    }

    public static TheoryItemBean createRecodItem(String id, String chapterText) {
        TheoryItemBean item = creatBaseItem(id);
        item.setChapterText(chapterText);
        item.setDownloadPath(FileUtils.generateDownloadPath(id));
        return item;
    }

    public static TheoryItemBean createOriginItem(String id, String chapterText) {
        TheoryItemBean item = creatBaseItem(id);
        item.setChapterText(chapterText);
        item.setDownloadPath("");
        return item;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public String getDownlaodText() {
        return downlaodText;
    }

    public void setDownlaodText(String downlaodText) {
        this.downlaodText = downlaodText;
    }

    public String getChapterText() {
        return chapterText;
    }

    public void setChapterText(String chapterText) {
        this.chapterText = chapterText;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getDownloadFileName() {
        return downloadFileName;
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    @Override
    public String toString() {
        return "TheoryItemBean{" +
                "id='" + id + '\'' +
                ", isDownload=" + isDownload +
                ", downlaodText='" + downlaodText + '\'' +
                ", chapterText='" + chapterText + '\'' +
                ", downloadPath='" + downloadPath + '\'' +
                '}';
    }
}
