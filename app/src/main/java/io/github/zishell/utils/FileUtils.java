package io.github.zishell.utils;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Created by zishell on 2015/8/16.
 */
public class FileUtils {
    /**
     * check the folder is exist
     * if not create
     *
     * @param path
     */
    public static void checkFolderExist(String path) {
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }

    public synchronized static void clearFolder(String path) {
        checkFolderExist(path);
        ArrayList<File> files = getFiles(path);
        if (files != null && files.size() > 0) {
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (file.exists() && file.isFile()) {
                    files.get(i).delete();
                }
            }
        }
    }

    public static ArrayList<File> getFiles(String folderPath) {
        ArrayList<File> files = new ArrayList<>();
        try {
            File[] allFiles = new File(folderPath).listFiles();
            if (allFiles != null && allFiles.length > 0) {
                for (int i = 0; i < allFiles.length; i++) {
                    File file = allFiles[i];
                    if (file.exists() && file.isFile()) {
                        files.add(file);
                    }
                    return files;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }

        } else {
            return 0;
        }
        return size;
    }

    // read the file to string
    public static String readFile(String fileName) {
        StringBuffer sb = new StringBuffer();
        try {
            String encoding = "UTF-8";
            File file = new File(fileName);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    sb.append(lineTxt + "\n");
                }
                read.close();
                return sb.toString();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return null;
    }

/*

    public static String readFile(String fileName) {

        String res = "";
        File file = new File(fileName);
        if (!file.exists()) return null;
        try {

            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];

            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return res;

    }
*/

    // true: append to the end of the file
    public static void writeStringToFile(String fileName, String content) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(fileName);
            byte[] bytes = content.getBytes("UTF-8");
            fout.write(bytes);
        } catch (IOException e) {
            Log.e("error", "==>write error!!!" + e.toString());
        } finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (IOException e) {
                Log.e("error", "==>write error!!!" + e.toString());
            }
        }
    }

    // true: append to the end of the file
    public static void rewriteStringToFile(String fileName, String content) {
        FileWriter writer = null;
        try {
            // true: append to the end of the file
            // false: re write the file with new string
            writer = new FileWriter(fileName, false);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // 写数据txt
    public static void writeFile(String filePath, String fileName,
                                 String writestr) throws IOException {
        try {
            File fileDir = new File(filePath);// 创建文件夹
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            FileOutputStream fout2 = new FileOutputStream(filePath + "/"
                    + fileName);

            byte[] bytes = writestr.getBytes();

            // fout.write(bytes);
            fout2.write(bytes);

            // fout.close();
            fout2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String arrayToString(ArrayList<String> array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.size(); i++) {
            sb.append(array.get(i));
            sb.append(" ");
        }
        return sb.toString();

    }

    public static String[] stringToArray(String str) {
        return str.split(" ");
    }


    /**
     * encodeBase64File:(将文件转成base64 字符串). <br/>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static String readBase64CodeFromFile(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.NO_WRAP);
    }

    /**
     * decoderBase64File:(将base64字符解码保存文件). <br/>
     *
     * @param base64Code 编码后的字串
     * @param savePath   文件保存路径
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static void writeBase64CodeToFile(String base64Code, String savePath) throws Exception {
        //byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        byte[] buffer = Base64.decode(base64Code, Base64.NO_WRAP);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }


    /**
     * reference to : (http://stackoverflow.com/questions/3752359/gzip-in-android)
     * compress string with gzip, return byte
     *
     * @param string
     * @return
     * @throws IOException
     */
    public static byte[] compress(String string) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }

    /**
     * decompress the compressed data to string
     *
     * @param compressed
     * @return
     * @throws IOException
     */
    public static String decompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }

    /**
     * read file under the assets folder and rewrite it into sdcard folder
     *
     * @param inputStream
     * @param newFilePath
     * @return
     */
    public static File createFileFromInputStream(InputStream inputStream, String newFilePath) throws IOException {
        File file = new File(newFilePath);
        OutputStream outputStream = new FileOutputStream(file);
        byte buffer[] = new byte[1024];
        int length = 0;

        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();
        return file;
    }

    public static void printAssetsFiles(Context context) {
        AssetManager am = context.getAssets();
        try {
            String[] list = am.list("");
            for (int i = 0; i < list.length; i++) {
                Log.i("FileUtils", "==>file in the assets : " + list[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray readSensorFile(final String filePath) {
        try {
            FileReader fileReader = new FileReader(new File(filePath));
            BufferedReader br = new BufferedReader(fileReader);
            String line = null;
            JSONArray sensors = new JSONArray();
            JSONObject detectResults = new JSONObject();
            while ((line = br.readLine()) != null) {
                try {
                    JSONObject obj = new JSONObject(line);
                    sensors.put(obj);

                } catch (Exception e) {
                    continue;
                }
            }
            return sensors;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
