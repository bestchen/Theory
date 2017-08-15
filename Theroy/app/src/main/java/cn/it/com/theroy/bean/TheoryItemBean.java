package cn.it.com.theroy.bean;

import java.io.Serializable;

import cn.it.com.theroy.uitls.FileUtils;


public class TheoryItemBean implements Serializable {

    public final static String TAG_RECORD = "tag_record";
    public final static String TAG_ORIGIN = "tag_origin";
    private String id = "";
    private boolean isDownload = false;
    private String downloadTip;
    private String chapterText;
    private String downloadPath;
    private String downloadFileName = id;
    private String tag = "";
    private int index;

    private static TheoryItemBean creatBaseItem(String id, String downloadTip, String tag, int index) {
        TheoryItemBean item = new TheoryItemBean();
        item.setId(id);
        item.setDownloadTip(downloadTip);
        item.tag = tag;
        item.index = index;
        item.setDownload("已经下载".equals(downloadTip));
        return item;
    }

    public static TheoryItemBean createRecodItem(String id, String downloadTip, String chapterText, String tag, int index) {
        TheoryItemBean item = creatBaseItem(id, downloadTip, tag, index);
        item.setChapterText(chapterText);
        item.setDownloadPath(FileUtils.generateDownloadPath(id));
        return item;
    }

    public static TheoryItemBean createOriginItem(String id, String chapterText, String tag, int index) {
        TheoryItemBean item = creatBaseItem(id, "", tag, index);
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

    public String getDownloadTip() {
        return downloadTip;
    }

    public void setDownloadTip(String downloadTip) {
        this.downloadTip = downloadTip;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
