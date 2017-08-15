package cn.it.com.theroy.uitls;

import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.download.db.FileHelper;
import cn.it.com.theroy.iview.TheroyApplication;

public class FileUtils {

    private static final int ROW = 3;
    private static final int PAGE_COUNT = 300;

    public static String getRecordAudioPath(String id) {
        return UIUitls.getContext().getFilesDir().getPath() + "_record_" + "audio_" + id;
    }

    public static String getRecordPath(String id) {
        return UIUitls.getContext().getFilesDir().getPath() + "_record_" + "txt_" + id;
    }

    public static String getOriginPath(String id) {
        return UIUitls.getContext().getFilesDir().getPath() + "_origin_" + "txt" + id;
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
            if (!file.exists()) {
                file.createNewFile();
            }
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

    public static String[] getAssertsFileName(final String path) {
        try {
            return TheroyApplication.getContext().getAssets().list(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getTxtContents(String fileName) {
        InputStream is = null;
        BufferedReader br = null;
        List<String> txts = new ArrayList<>();
        try {
            is = TheroyApplication.getContext().getAssets().open(fileName);
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = "";
            int index = 0;
            while (index < ROW && (line = br.readLine()) != null) {
                sb.append(line);
                index++;
                if (index == ROW) {
                    index = 0;
                    txts.add(sb.toString());
                    sb = new StringBuilder();
                }
            }
            if (index < 50) {
                txts.add(sb.toString());
            }
            return txts;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(is);
            closeStream(br);
        }
        return null;
    }

    public static boolean copyAssets(String assetDir, String dir) {
        String[] files;
        AssetManager assets = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            assets = TheroyApplication.getContext().getAssets();
            files = assets.list(assetDir);
            File mWorkingPath = new File(dir);
            //if this directory does not exists, make one.
            if (!mWorkingPath.exists()) {
                mWorkingPath.mkdirs();
            }

            for (int i = 0; i < files.length; i++) {
                String fileName = files[i];
                //we make sure file name not contains '.' to be a folder.
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        copyAssets(fileName, dir + fileName + "/");
                    } else {
                        copyAssets(assetDir + "/" + fileName, dir + fileName + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }
                if (0 != assetDir.length()) {
                    in = assets.open(assetDir + "/" + fileName);
                } else {
                    in = assets.open(fileName);
                }
                out = new FileOutputStream(outFile);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
            closeStream(out);
        }
        return false;
    }

    public static String getDirPath() {
        return TheroyApplication.getContext().getFilesDir().getAbsolutePath() + "/";
    }

    public static String checkDownloaded(String fileName) {
        String path = FileHelper.getFileDefaultPath() + "/" + fileName + ".zip";
//        if ("1".equals(fileName)) {
//            upZipFile(path, FileHelper.audioUnZipPath);
//            File f = new File(FileHelper.audioUnZipPath + "/" + fileName + "/");
//            String[] list = f.list();
//            if (list != null) {
//                for (String s : list) {
//                    Log.e("tag", "s..." + s);
//                }
//            }
//        }
        File dirs = new File(path);
        if (!dirs.exists()) {
            return "下载";
        }
        return "已经下载";
    }

    /**
     * 含子目录的文件压缩
     *
     * @throws Exception
     */
    // 第一个参数就是需要解压的文件，第二个就是解压的目录
    public static boolean upZipFile(String zipFile, String folderPath) {
        final int buffer_size = 1024 * 1024;
        ZipFile zfile = null;
        try {
            // 转码为GBK格式，支持中文
            zfile = new ZipFile(zipFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[buffer_size];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            // 列举的压缩文件里面的各个文件，判断是否为目录
            if (ze.isDirectory()) {
                String dirstr = folderPath + ze.getName();
                dirstr.trim();
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os = null;
            FileOutputStream fos = null;
            // ze.getName()会返回 script/start.script这样的，是为了返回实体的File
            File realFile = getRealFileName(folderPath, ze.getName());
            try {
                fos = new FileOutputStream(realFile);
            } catch (FileNotFoundException e) {
                return false;
            }
            os = new BufferedOutputStream(fos);
            InputStream is = null;
            try {
                is = new BufferedInputStream(zfile.getInputStream(ze));
            } catch (IOException e) {
                return false;
            }
            int readLen = 0;
            // 进行一些内容复制操作
            try {
                while ((readLen = is.read(buf, 0, 1024)) != -1) {
                    os.write(buf, 0, readLen);
                }
            } catch (IOException e) {
                return false;
            }
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                return false;
            }
        }
        try {
            zfile.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        absFileName = absFileName.replace("\\", "/");
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                ret = new File(ret, substr);
            }

            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            ret = new File(ret, substr);
            return ret;
        } else {
            ret = new File(ret, absFileName);
        }
        return ret;
    }
}