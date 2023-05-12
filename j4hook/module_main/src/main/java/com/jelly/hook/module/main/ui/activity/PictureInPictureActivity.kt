package com.jelly.hook.module.main.ui.activity

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Rational
import android.view.View
import androidx.annotation.RequiresApi
import com.jelly.hook.module.base.ui.BaseActivity
import com.jelly.hook.module.base.utils.ToastUtil
import com.jelly.hook.module.main.R
import com.jelly.hook.module.main.databinding.MainActPicInPicBinding

class PictureInPictureActivity : BaseActivity() {
    private lateinit var mBinding: MainActPicInPicBinding
    private var mReceiver: BroadcastReceiver? = null

    companion object {
        private const val TAG = "PictureInPictureActivity"
        private val ACTION_MEDIA_CONTROL = "media_control"
        private val EXTRA_CONTROL_TYPE = "control_type"
        private val CONTROL_TYPE_PLAY = 1
    }

    override fun getLayoutView(): View {
        mBinding = MainActPicInPicBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initListener() {
        super.initListener()
        mBinding.tvwTest1.setOnClickListener {
            ToastUtil.showShort("tvwTest1")
            val isSupportPipMode =
                packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            Log.e(
                TAG,
                "binding.tvwTest1 isSupportPipMode:$isSupportPipMode"
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var mPictureInPictureParamsBuilder = PictureInPictureParams.Builder()

                //设置param宽高比,根据宽高比例调整初始参数

                //设置param宽高比,根据宽高比例调整初始参数
                val aspectRatio =
                    Rational(mBinding.lltContent.getWidth(), mBinding.lltContent.getHeight())
                mPictureInPictureParamsBuilder.setAspectRatio(aspectRatio)

                val actions = ArrayList<RemoteAction>()
                val intent: Intent = Intent(ACTION_MEDIA_CONTROL).putExtra(
                    EXTRA_CONTROL_TYPE,
                    CONTROL_TYPE_PLAY
                )
                intent.data = Uri.parse("https://www.baidu.com/2")
                val pendingIntent = PendingIntent.getBroadcast(
                    this@PictureInPictureActivity,
                    1,
                    intent,
                    0
                )
                actions.add(
                    RemoteAction(
                        Icon.createWithResource(it.getContext(), R.drawable.ic_launcher),
                        "标题1", "标题2", pendingIntent
                    )
                )

                mPictureInPictureParamsBuilder.setActions(actions)
                val enterPictureInPictureMode =
                    enterPictureInPictureMode(mPictureInPictureParamsBuilder.build())
                Log.e(
                    TAG,
                    "binding.tvwTest1 enterPictureInPictureMode:$enterPictureInPictureMode"
                )
            }
        }

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // 这里是没反应的，没有具体去调试
                Log.e(TAG, "BroadcastReceiver onReceive")
                if (intent == null) {
                    return
                }
                if (ACTION_MEDIA_CONTROL != intent.action) {
                    return
                }
                // This is where we are called back from Picture-in-Picture action
                val controlType = intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)
                try {
                    when (controlType) {
                        CONTROL_TYPE_PLAY -> Log.e(
                            TAG,
                            "BroadcastReceiver onReceive CONTROL_TYPE_PLAY"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        registerReceiver(mReceiver, IntentFilter(ACTION_MEDIA_CONTROL))
    }


    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        mReceiver = null
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        Log.e(
            "PictureInPictureActivity",
            "onPictureInPictureModeChanged isInPictureInPictureMode:$isInPictureInPictureMode,newConfig:$newConfig"
        )
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.e(TAG, "onUserLeaveHint")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.e(TAG, "onBackPressed")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e(TAG, "onNewIntent intent:$intent")
    }
}