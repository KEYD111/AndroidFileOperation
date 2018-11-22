package com.example.dadac.getfilepath;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 测试安卓文件的读写
 * 路径的选择
 * 文件/文件夹的操作
 * 数据的读写
 */
public class MainActivity extends AppCompatActivity {

    private String LOG_Info = "dachenI";
    private String LOG_Debug = "dachenD";
    private String LOG_Error = "dachenE";

    private ImageView DC_ImageViewShow;

    //SharedPrference
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //Assets
    private AssetManager assetManager;
    MediaPlayer mPlayer;

    /*-----------------  SQLite  --------------------------*/
    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assetManager = getAssets();
        mPlayer = new MediaPlayer();

        /*--------------- SharedPreferences test ------------------*/
      /*  preferences = getSharedPreferences("SharedPreferenceTest", MODE_PRIVATE);
        editor = preferences.edit();
        WriteToSharedPrefernces();
        ReadFromSharedPrefernces();*/

        /*--------------------文件流的操作test------------*/
        // WriteDataToStorage("我想写的东西 Buffer", TestFilePathApkPrivate(getApplicationContext(), "SQLite"), "a.txt", 3, 0);
        //  WriteDataToStorage("我想写的东西 Buffer", TestFilePathExternalData("SQLite"), "abcd.txt", 3, 1);
        //        try {
        //            ReadDataFromStorage(TestFilePathExternalData("SQLite"), "abcd.txt", 2);
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        //ListFileDirName(TestFilePathExternalData("SQLite"));

        /*----------------- Assets的操作 test-----------*/
        getTextFromAssets("dog.png", 1);
        /*----------------------- raw 文件的操作test-------*/
        getTextFromRaw();

    }

    /*-------------------------------- Assets的操作 ---------------------------------------------------*/

    /**
     * @param fileName:  文件名字
     * @param index:文件类型 文本   图片  音乐  0 1 2
     * @Function: 读取 Assets下的文件
     * @Return:
     */
    public void getTextFromAssets(String fileName, int index) {
        if (index == 0) { //文本操作
            String Result = "";
            try {
                InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
                BufferedReader bufReader = new BufferedReader(inputReader);
                String line = "";
                while ((line = bufReader.readLine()) != null)
                    Result += line;
                Log.i(LOG_Info + "Assets", Result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (index == 1) {  //图片操作
            Bitmap bitmap = null;
            InputStream is = null;
            try {
                is = getAssets().open(fileName);
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            DC_ImageViewShow = (ImageView) findViewById(R.id.DC_ImageViewShow);
            DC_ImageViewShow.setImageBitmap(bitmap);
        } else if (index == 2) {   //音乐操作
            try {
                // 打开指定音乐文件,获取assets目录下指定文件的AssetFileDescriptor对象
                AssetFileDescriptor afd = assetManager.openFd(fileName);
                mPlayer.reset();
                // 使用MediaPlayer加载指定的声音文件。
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                // 准备声音
                mPlayer.prepare();
                // 播放
                mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Log.i(LOG_Error, "ERROR");

    }

    /*-------------------------------- raw 文件的操作 ---------------------------------------------------*/
    public String getTextFromRaw() {
        String Result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().openRawResource(R.raw.abc));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";

            while ((line = bufReader.readLine()) != null)
                Result += line;
            Log.i(LOG_Info + "Raw", Result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result;
    }

    /*-------------------------------- SharedPreferences ---------------------------------------------------*/
    //从 SharedPreferences在中读出数据
    private void ReadFromSharedPrefernces() {

        // 读取字符串数据
        String time = preferences.getString("time", null);
        // 读取int类型的数据
        int randNum = preferences.getInt("random", 0);
        String result = time == null ? "您暂时还未写入数据" : "写入时间为："
                + time + "\n上次生成的随机数为：" + randNum;
        // 使用Toast提示信息
        Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
    }

    //往SharedPreferences中写入数据
    private void WriteToSharedPrefernces() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 "
                + "hh:mm:ss");
        // 存入当前时间
        editor.putString("time", sdf.format(new Date()));        // 存入一个随机数
        editor.putInt("random", (int) (Math.random() * 100));
        // 提交所有存入的数据
        editor.commit();
    }

    /*---------------------------文件流的操作-----------------------------------------------*/

    /**
     * @Function: 常见的文件路径的读取只是简单的介绍一下（没什么用）
     * 根据自己的需要，根据实际的要求来进行操作。私有 和 APP绑定  生成文件
     * 简单的测试几个路径的例子
     * @Return:
     */
    private void getFilePath(String definedPath) {
        String a = Environment.getDataDirectory().toString();
        String b = getFilesDir().getAbsolutePath();
        String c = getCacheDir().getAbsolutePath();
        String d = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
        String e = Environment.getExternalStorageDirectory().getPath();
        String f = getExternalFilesDir("Documents").getPath();
        Log.i(LOG_Info + "a: ", "Environment.getDataDirectory().toString():-----" + a);
        Log.i(LOG_Info + "b: ", "getFilesDir().getAbsolutePath():----- " + b);
        Log.i(LOG_Info + "c: ", "getCacheDir().getAbsolutePath():----- " + c);
        Log.i(LOG_Info + "d: ", "Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath():----- " + d);
        Log.i(LOG_Info + "e: ", "Environment.getExternalStorageDirectory().getPath():----- " + e);
        Log.i(LOG_Info + "f: ", "getExternalFilesDir(\"Documents\").getPath():----- " + f);
    }

    /**
     * 会了这常见的三种方式足够了
     * 一种是跟 app 绑定在一起的 数据库SQLite操作的时候需要使用  跟随着APP的生命周期而变换 apk私有目录 不会导致数据残留  卸载就没了  但是重新烧录不会改变数据库   数据库操作（亲测）
     * 一种是存在内存当中的，会一直存在  图片保存 数据库操作 （亲测）Environment 方式获取路径 公共目录
     * 还有一种就是挂载的SD卡 这类就比较烦  而且是特别烦，除非必须使用，一般不推荐使用 容易出错  现在的手机不支持外部SD卡了， 保存图片 亲测可以用
     */
    /**
     * @param FileDirName：你想创建的文件夹的名字
     * @Function: 测试内部存储
     * @attention: 数据跟APP绑定 app卸载后就没有了
     * 生成的文件 存储在 NANDFlash --> Android --> data   里面是的 app的包名 com.XXX.......---> files 这种格式 找到对应的就可以了
     * @Return: 返回文件夹的路径，可以在文件夹下继续创建文件
     */
    private String TestFilePathApkPrivate(Context context, String FileDirName) {
        //不需要挂载测试，因为 app 都可以装 为什么 会没有数据
        String filedirpath = context.getExternalFilesDir(FileDirName).getPath();  //文件夹
        File fileDir = new File(filedirpath);                   //创建文件夹
        if (fileDir.exists()) {    //判断文件是否存在  很重要  别每次都会去覆盖数据
            fileDir.setWritable(true);
            Log.i(LOG_Info, "文件夹已经存在    TestFilePathInternalData（）");
        } else {
            try {
                fileDir.mkdir();
                Log.i(LOG_Info, "文件夹创建成功    TestFilePathExternalData（）");
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(LOG_Error, "文件夹创建错误   TestFilePathExternalData()" + e.getMessage());
            }
        }
        return filedirpath;
    }

    /**
     * @param filesname: 文件夹的名字
     * @Function: 测试内部存储  在公共存储目录下新建文件夹
     * @attention: 一直存在 app卸载后依然存在 存储在文件的公共目录下的 比如说 打开 NANDFlash 会出现 Movies Pictures Downlaod 等等
     * @Return:
     */
    private String TestFilePathExternalData(String filesname) {
        String pulicfileDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { //测试是否挂载SD卡，并且是否加载了权限
            pulicfileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
            File filedir = new File(pulicfileDir, filesname);
            if (filedir.exists()) {
                Log.i(LOG_Info, "文件夹已经存在     TestFilePathExternalData（）");
            } else {
                try {
                    filedir.mkdir();
                    Log.i(LOG_Info, "文件夹创建成功    TestFilePathExternalData（）");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(LOG_Error, "文件夹创建错误   TestFilePathExternalData()" + e.getMessage());
                }
            }
        } else
            Log.i(LOG_Error, "没有挂载SD卡或是没有打开权限");
        return (pulicfileDir + File.separator + filesname);     //返回的是文件的目录
    }

    /**
     * @Function: 外部SD卡
     * @attention: 注意getExternal 和 SD 卡是不一样的 这种骚气的写法，在我的开发板上测试可以，但是强烈不推荐大家去使用
     * @Return:
     */
    private void TestFilePathSDCard() {
        File fileDir1 = new File("/mnt/sdcard/SQLite1");    //这是内部挂载的SD卡  和 公共的是同级目录，无法靠代码获取，经验
        File fileDir2 = new File("/mnt/sdcard2/SQLite2");    //这是外部插卡式的SD卡，ARM板上支持，绝大部分手机已经凉了
        fileDir1.mkdir();
        fileDir2.mkdir();
    }

    /**
     * @param content:    要写的内容
     * @param filedirname 文件夹的名字
     * @param filename:   文件的名字
     * @param mode:       以什么方式往里面去写 0 1 2 3
     * @param ways:       两种方式 Buffer RandomAccessFile  Print  0 1 2
     * @Function: 将content写到指定的文件的指定的目录下去
     * @Return:
     */
    private void WriteDataToStorage(String content, String filedirname, String filename, int mode, int ways) {
        String FileName = filedirname + File.separator + filename;   //拼接字符串  文件的存储路径
        File subfile = new File(FileName);  //文件夹路径和文件路径   判断文件是否存在
        if (subfile.exists()) {
            subfile.setWritable(true);
            boolean readable = subfile.canRead();
            boolean writeable = subfile.canWrite();
            Log.i(LOG_Info, "文件创建成功" + "readable:" + readable + " writeable:" + writeable);
        } else {
            try {
                subfile.createNewFile();
            } catch (IOException e) {
                Log.i(LOG_Error, "文件创建出错  " + e.getMessage());
                e.printStackTrace();
            }
        }
        int Context_Mode = mode;
        int Ways = ways;
        if (Context_Mode == 0) {
            Context_Mode = Context.MODE_PRIVATE;  //该文件只能被当前程序读写。
        } else if (Context_Mode == 1) {
            Context_Mode = Context.MODE_APPEND;   //以追加方式打开该文件，应用程序可以向该文件中追加内容。
        } else if (Context_Mode == 2) {
            Context_Mode = Context.MODE_WORLD_READABLE;  //该文件的内容可以被其他应用程序读取。
        } else if (Context_Mode == 3) {
            Context_Mode = Context.MODE_WORLD_WRITEABLE;  //该文件的内容可由其他程序读、写。
        } else {
            Context_Mode = Context.MODE_WORLD_WRITEABLE;  //省的烦   反正都可以读
        }
        if (Ways == 0) {
            Log.i(LOG_Info, "BufferWriter");
            FileOutputStream fileOutputStream = null;
            BufferedWriter bufferedWriter = null;
            OutputStreamWriter outputStreamWriter = null;
            try {
                //fileOutputStream = openFileOutput(FileName, Context_Mode);  contains a path separator 报错
                fileOutputStream = new FileOutputStream(subfile);
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "utf-8"));  //解决输入中文的问题
                bufferedWriter.write(content + "\t");
                bufferedWriter.flush();
                bufferedWriter.close();
                //outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");   //两种方式都可以
                //outputStreamWriter.write(content);
                //outputStreamWriter.flush();
                //outputStreamWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(LOG_Error, "写入数据出错 " + e.getMessage());
            } finally {
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (Ways == 1) {
            Log.i(LOG_Info, "RandomAccessFile");
            try {
                RandomAccessFile raf = new RandomAccessFile(subfile, "rw");
                raf.seek(subfile.length());
                raf.write(content.getBytes());
                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(LOG_Error, "写入数据出错 " + e.getMessage());
            }
        } else if (Ways == 2) {
            Log.i(LOG_Info, "Printer");
            try {
                FileOutputStream fileoutputStream = new FileOutputStream(subfile);
                //openFileOutput("text2", Context.MODE_PRIVATE);
                PrintStream ps = new PrintStream(fileoutputStream);
                ps.print(content + "\t");
                ps.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else
            Ways = 0;
    }


    /**
     * @param fileDirName: 文件夹的路径
     * @Function: 列出文件夹下所有文件的名字
     * @Return:
     */
    private File[] ListFileDirName(String fileDirName) {
        File fileDir = new File(fileDirName);
        File[] files = new File[0];
        if (fileDir.isDirectory()) {
            files = fileDir.listFiles();
        }
        for (File a : files) {   //可以利用适配器做成界面  完成为了玩没意思
            Log.i(LOG_Info, a.toString());
        }
        return files;
      /*  /storage/emulated/0/Documents/SQLite/abcd.txt  手机的测试结果
          /mnt/internal_sd/Documents/SQLite/abcd.txt ARM板的测试结果*/
    }


    /**
     * @param fileDirName:文件夹目录
     * @param fileName:文件名字
     * @param ways:读取文件的方式
     * @Function: 从存储路径中读出数据
     * @Return:
     */
    private void ReadDataFromStorage(String fileDirName, String fileName, int ways) throws IOException {
        File file = new File(fileDirName, fileName);
        int Ways = ways;
        if (Ways == 0) {
            Log.i(LOG_Info, "FileInputStream");
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[fileInputStream.available()];
                fileInputStream.read(bytes);
                String result = new String(bytes);
                Log.i(LOG_Info, "读取的内容是：" + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Ways == 1) {   //最好使用 Buffer 缓冲流，安全机制 大量的文件
            Log.i(LOG_Info, "Bufferreader");
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String readline = "";
                StringBuffer stringBuffer = new StringBuffer();
                while ((readline = bufferedReader.readLine()) != null) {
                    stringBuffer.append(readline);
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                    Log.i(LOG_Info, "读取的内容是：" + stringBuffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Ways == 2) {
            Log.i(LOG_Info, "Input+Buffer");
            try {
                String fileContent = null;
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader reader = new BufferedReader(read);
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent += line;
                }
                reader.close();
                read.close();
                Log.i(LOG_Info, fileContent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(LOG_Error, e.getMessage());
            }
        } else
            Ways = 2;
    }

    /**
     * @param file: 文件/文件夹的路径
     * @Function: 文件夹  文件的删除
     * @Return:
     */
    private void DeleteFileDirORFile(File file) {
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    return;
                }
                if (childFile.length > 1) {
                    for (File f : childFile) {
                        DeleteFileDirORFile(f);
                    }
                }
            }
        }
    }



    /*-------------------------------- SQLite的操作 ---------------------------------------------------*/

    /**
     * @Function: 初始化 DaoConfig配置
     * @Return:
     */
    private void InitDaoConfig() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("mydbutils.db")    //设置数据库的名字，默认是xutils.db
                .setDbDir(new File("/mnt/sdcard/SQLite"))    //设置数据库路径，默认存储在 app的私有目录
                .setDbVersion(1)    //设置当前的版本号
                .setDbOpenListener(new DbManager.DbOpenListener() {   //设置数据库打开的监听
                    @Override
                    public void onDbOpened(DbManager db) {
                        //开启数据库支持多线程操作，提升性能，对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                }).setDbUpgradeListener(new DbManager.DbUpgradeListener() {    //设置数据库更新的监听
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        Log.i("dachen", "数据库已经更新了 oldversion: " + oldVersion + "  newversion" + newVersion);
                    }
                }).setTableCreateListener(new DbManager.TableCreateListener() {   //设置表创建的监听
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                        Log.i("dachen", "表已经被创建了" + table.getName());
                    }
                }).setAllowTransaction(true);    //设置是否允许事务，默认true
        dbManager = x.getDb(daoConfig);
    }

    /**
     * @Function: 创建数据库
     * @Return:
     */
    private void CreateSQLData() {
        ArrayList<ChildInfo> childInfos = new ArrayList<>();
        childInfos.add(new ChildInfo("Li", 10.0));
        childInfos.add(new ChildInfo("Jia", 20.0));
        childInfos.add(new ChildInfo("Wen", 30.0));
        childInfos.add(new ChildInfo("Da", 40.0));
        childInfos.add(new ChildInfo("Chen", 50.0));
        try {
            dbManager.save(childInfos);
            Log.i("dachen", "数据库已经创建成功");
        } catch (DbException e) {
            e.printStackTrace();
            Log.e("dachenE", e.getMessage());
        }
    }

    /**
     * @Function: 查询表中的数据
     * @Return:
     */
    private void querySQLData() {
        try {
            ChildInfo firstdata = dbManager.findFirst(ChildInfo.class);
            Log.i("dachen", "第一条数据是" + firstdata.toString());

            //添加查询条件进行查询
            List<ChildInfo> childInfos = dbManager.selector(ChildInfo.class)
                    .where("_id", ">", 4)
                    .and("salary", ">", 49.0)
                    .findAll();
            for (ChildInfo a : childInfos) {
                Log.i("dachen", childInfos.toString());
            }

        } catch (DbException e) {
            e.printStackTrace();
            Log.e("dachen", "不好意思找不到");
        }
    }

    /**
     * @Function: 删除数据库
     * @Return:
     */
    private void deleteSQL() {
        try {
            dbManager.dropDb();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Function: 删除表
     * @Return:
     */
    private void deletetable() {
        try {
            dbManager.dropTable(ChildInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Function: 新增表中的数据
     * @Return:
     */
    private void newSQLData() {
        try {
            ChildInfo childInfo = new ChildInfo("LOVE", 1000.0);
            dbManager.save(childInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Function: 修改表中的数据
     * @Return:
     */
    private void updateSQLData() {
        try {
            //第一种写法
            ChildInfo childInfo = dbManager.findFirst(ChildInfo.class);
            childInfo.setSalary(1888.0);
            dbManager.update(childInfo, "u_name");   // u_salary   表中的字段名

            //第二种写法
            WhereBuilder builder = WhereBuilder.b();
            builder.and("_id", "=", childInfo.getId());   //构造修改的条件
            KeyValue name = new KeyValue("u_name", "jiajia");
            dbManager.update(ChildInfo.class, builder, name);

            //第三种写法
            childInfo.setName("dada");
            dbManager.saveOrUpdate(childInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    /**
     * @Function: UI 异步执行
     * @Return:
     */
    private void AsyUISQL() {
        x.task().run(new Runnable() {
            @Override
            public void run() {
                //异步代码
            }
        });
    }

    /**
     * @Function: 同步代码
     * @Return:
     */
    private void TogetherUISQL() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                //同步代码

            }
        });
    }


}























