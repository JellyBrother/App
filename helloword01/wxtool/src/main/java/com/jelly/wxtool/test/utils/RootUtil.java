package com.jelly.wxtool.test.utils;

import com.jelly.baselibrary.utils.LogUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * //在IO流中输入命令，需要加"\n"，因为见到回车才会执行命令；
 * //如果直接用Runtime.getRuntime().exec(adbCommand)来执行，则不必加回车符；
 * //点击屏幕上的一点，eg：这点的像素坐标是（100,100）
 * public String AdbTap = "input tap 100 100\n";
 * //实现滑动操作，前两个参数是开始坐标，接下来两个是终点坐标，最后一个是持续时间。
 * 解释参考：http://blog.csdn.net/u012912435/article/details/51483309
 * 可以用来模拟长按，原理：在小的距离内，在较长的持续时间内进行滑动，最后表现出来的结果就是长按动作。
 * public String AdbSwipe="input swipe 500 500 501 501 2000\n";
 * //按下按键，eg：该按键的按键值是4（系统的返回键）。按键值参考https://www.cnblogs.com/sharecenter/p/5621048.html
 * public String AdbKeyevent="input keyevent 4\n";
 * //输入文本，eg：文本内容是1234567890
 * public String AdbText="input text 1234567890\n";
 */
public class RootUtil {
    private static final String TAG = "RootUtil";
    private static boolean mHaveRoot = false;

    /**
     * 如果之前没为应用分配root权限，到系统里为它分配root权限
     * 应用程序运行命令获取 Root权限，设备必须已破解(已Root过，获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean isRoot() {
        OutputStream os = null;
        try {
            //请求进入su账户，类似PC端adb shell之后的su命令。同时，获取与之相关的输出流
            os = Runtime.getRuntime().exec("su").getOutputStream();
            //退出su
            os.write(("exit\n").getBytes());
            os.flush();
            //上述命令执行成功，则进入su账户成功，具备进入su的能力，说明已经获取到了root权限
            return true;
        } catch (Exception e) {
            LogUtil.getInstance().e(TAG, "isRoot Exception：" + e.toString());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 执行adb命令，需要已经为应用分配过root权限
     *
     * @param adbExec
     */
    public static void execAdb(String adbExec) {
        //简单有效，直接执行三条条adb命令
//      try {
//          Runtime.getRuntime().exec("su");
//          Runtime.getRuntime().exec(adbCommand);
//          Runtime.getRuntime().exec("exit\n");
//      } catch (Exception e1) {
//          e1.printStackTrace();
//      }

        //或者用下面方式
        OutputStream os = null;
        try {
            //获取与之相关的输出流
            os = Runtime.getRuntime().exec("su").getOutputStream();
            os.write(adbExec.getBytes());
            //退出su
            os.write(("exit\n").getBytes());
            os.flush();
        } catch (Exception e) {
            LogUtil.getInstance().e(TAG, "execAdb Exception：" + e.toString());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 判断机器Android是否已经root，即是否获取root权限
     */
    public static boolean haveRoot() {
        if (!mHaveRoot) {
            int ret = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
            if (ret != -1) {
                LogUtil.getInstance().i(TAG, "have root!");
                mHaveRoot = true;
            } else {
                LogUtil.getInstance().i(TAG, "not root!");
            }
        } else {
            LogUtil.getInstance().i(TAG, "mHaveRoot = true, have root!");
        }
        return mHaveRoot;
    }

    /**
     * 执行命令并且输出结果
     */
    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            LogUtil.getInstance().i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                LogUtil.getInstance().d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 执行命令但不关注结果输出
     */
    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());

            LogUtil.getInstance().i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}