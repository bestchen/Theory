package cn.it.com.theroy.uitls;

import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cn.it.com.theroy.bean.TheoryItemBean;

/**
 * Created by Chenweiwei on 2017/7/30.
 */

public class FileUtils {

    public static String getRecordAudioPath(String id) {
        return UIUitls.getContext().getFilesDir() + File.separator + "record_" + File.separator + "audio_" + File.separator + id;
    }

    public static String getRecordPath(String id) {
        return UIUitls.getContext().getFilesDir() + File.separator + "record_" + File.separator + "txt_" + File.separator + id;
    }

    public static String getOriginPath(String id) {
        return UIUitls.getContext().getFilesDir() + File.separator + "origin_" + File.separator + "txt" + File.separator + id;
    }

    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static TheoryItemBean readTheoryItem(String path) {
        TheoryItemBean items = null;
        ObjectInputStream ois = null;
        try {
            //获取输入流
            ois = new ObjectInputStream(new FileInputStream(new File(path)));
            items = (TheoryItemBean) ois.readObject();
            //获取文件中的数据
            return items;
            //把数据显示在TextView中
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeStream(ois);
        }
    }

    //保存数据
    public static void saveTheoryItem(String path, TheoryItemBean bean) {
        ObjectOutputStream fos = null;
        try {
            //如果文件不存在就创建文件
            File file = new File(path);
            //file.createNewFile();
            //获取输出流
            //这里如果文件不存在会创建文件，这是写文件和读文件不同的地方
            fos = new ObjectOutputStream(new FileOutputStream(file));
            //这里不能再用普通的write的方法了
            //要使用writeObject
            fos.writeObject(bean);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(fos);
        }
    }

    public static String generateDownloadPath(String id) {
        return "http://guanglun.vguide.com.cn/part" + Integer.valueOf(id) + ".zip";
    }
}
