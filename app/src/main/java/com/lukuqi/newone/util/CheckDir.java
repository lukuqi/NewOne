package com.lukuqi.newone.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * 检查目录结构是否完整
 * <p>
 * Created by mr.right on 2016/4/11.
 */
public class CheckDir {
    //    static String basePath = Environment.getExternalStorageState() + "/NewOne";
    static File basePath = new File(Environment.getExternalStorageDirectory(), "NewOne"); //应用主文件夹路径
    static File imagePath = new File(basePath, "Image");//图片文件夹路径

    /**
     * 应用主文件夹
     *
     * @param basePath 主文件夹路径
     */
    public static void checkBasePath(File basePath) {
        if (!basePath.exists()) {
            basePath.mkdir();
        }
    }

    /**
     * 图片文件夹
     *
     * @param image 图片路径
     * @return
     * @throws IOException
     */
    public static File checkImageFile(String image) throws IOException {
        checkBasePath(imagePath);
        File file = new File(imagePath, image);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;

    }

    public static void checkFile(File file) throws IOException {
        checkBasePath(basePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }
}
