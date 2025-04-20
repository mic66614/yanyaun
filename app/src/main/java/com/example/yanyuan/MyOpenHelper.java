package com.example.yanyuan;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyOpenHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "yanyuan.db"; // 数据库文件名
    private static String DB_PATH = "/data/data/com.example.yanyuan/databases/"; // 数据库在设备上的路径
    private Context context;

    public MyOpenHelper(Context context) {
        super(context, "yanyuan.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void copyDataBase(){
        // 检查数据库文件是否已经存在
        File databaseFile = context.getDatabasePath(DB_NAME);
        if (!databaseFile.exists()) {
            try {
                // 从assets复制数据库文件
                AssetManager assetManager = context.getAssets();
                InputStream in = assetManager.open(DB_NAME);
                OutputStream out = new FileOutputStream(databaseFile);

                // 复制文件
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                // 关闭流
                in.close();
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
