package com.dwin.activity;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Proxy;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by 58255 on 2016/8/6.
 */
public class DownloadThread extends Thread {

    Socket socket = null;
    int dataType = -1;
    int dataIndex = 0;
    int dataLength = 0;
    int datacomplete = -1;
    InputStream is;
    public DownloadThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            is = socket.getInputStream();

            byte[] data = new byte[20];
            int cnt=0;
            int tempData = 0;
            while((tempData=is.read())!=-1)
            {
                if((tempData==10)&&(data[cnt-1]==13))
                {
                    //处理代码,将byte转化为string，截取：号后的字符串
                    data[cnt-1]=0;
                    datacomplete++;
                    String str="";
                    switch (datacomplete)
                    {
                        case 0://读取的是数据类型
                            str = new String(data);
                            str = String.copyValueOf(str.toCharArray(), 0, cnt-1);
                            //System.out.println(str);
                            dataType = Integer.parseInt(str.substring(cnt - 2));
                            System.out.println("dataType:"+dataType);
                            cnt=0;
                            str="";
                            break;
                        case 1://读取的是索引
                            str = new String(data);
                            str = String.copyValueOf(str.toCharArray(), 0, cnt-1);
//                            System.out.println("索引"+str);
                            dataIndex = Integer.parseInt(str.substring(cnt - 2));
                            System.out.println("dataIndex:"+dataIndex);
                            cnt=0;
                            break;
                        case 2:
                            str = new String(data);
                            str = String.copyValueOf(str.toCharArray(), 0, cnt-1);
                            str = str.substring(str.indexOf(':')+1);
                            dataLength = Integer.parseInt(str);
//                            dataIndex = Integer.parseInt(str.substring(cnt - 2));
                            System.out.println("dataLength:"+dataLength);
                            //datacomplete=-1;
                            downloadData(dataType,dataIndex,dataLength);//下载数据
                            cnt=0;
                            break;//读取的是长度
                        default:break;
                    }




                    //System.out.println(str);

                    //如果头信息读取完毕则进入存储

                }
                if(datacomplete==2){
                    datacomplete=-1;
                    break;
                }
                data[cnt++]=(byte)tempData;
            }
            is.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void downloadData(int dataType,int dataIndex,int dataLength){
        File file;
        switch (dataType)
        {
            case 0://下载文本
                file = new File("/mnt/sdcard1/advertising.txt");
                System.out.println(file.getPath());
                try {
                    OutputStream out = new FileOutputStream(file);
                    //读取数据
                    int length=0;
                    byte[] date = new byte[10000];
                    while ((length = is.read(date)) != -1) {
                        System.out.println(new String(date));
                        out.write(date,0,length);
                    }
                    out.flush();
                    out.close();
                    System.out.println("广告传输完毕");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 1://下载图片
                file = new File("/mnt/sdcard1/test.jpg");
                System.out.println(file.getPath());
                try {
                    OutputStream out = new FileOutputStream(file);
                    //读取数据
                    int length=0;
                    byte[] date = new byte[10000];
                    while ((length = is.read(date)) != -1) {
                        System.out.println(new String(date));
                        out.write(date,0,length);
                    }
                    out.flush();
                    out.close();
                    System.out.println("图片传输完毕");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2://下载视频
                file = new File("/mnt/sdcard1/bell"+dataIndex+".mp4");
                System.out.println(file.getPath());
                try {
                    OutputStream out = new FileOutputStream(file);
                    //读取数据
                    int length=0;
                    byte[] date = new byte[10000];
                    while ((length = is.read(date)) != -1) {
                        System.out.println(new String(date));
                        out.write(date,0,length);
                    }
                    out.flush();
                    out.close();
                    System.out.println("视频传输完毕");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:break;
        }
    }
    public static String InputStreamTOString(InputStream in) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int count = -1;
        while((count = in.read(data,0,4096)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(),"gb2312");
    }
}
