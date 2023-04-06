package com.jelly.othertool.main.viewmodel;

import android.content.Context;
import android.os.Bundle;

import com.jelly.baselibrary.base.BaseLiveData;
import com.jelly.baselibrary.base.BaseViewModel;
import com.jelly.baselibrary.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class OtherToolMainActViewModel extends BaseViewModel {
    private static final String TAG = "OtherToolMainActViewModel";
    public BaseLiveData<String> mToastText;

    public OtherToolMainActViewModel() {
        mToastText = newLiveData();
    }

    @Override
    public void initData(Bundle bundle) {
    }

    @Override
    public void destroyData() {
        super.destroyData();
    }

    public void copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
                mToastText.postValue("复制成功");
            }
        } catch (Exception e) {
            mToastText.postValue("复制失败");
            LogUtil.getInstance().e(TAG, "exception:" + e.toString());
        }
    }
}
