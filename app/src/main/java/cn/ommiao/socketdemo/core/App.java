package cn.ommiao.socketdemo.core;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import cn.ommiao.socketdemo.BuildConfig;

public class App extends Application {

    private static App mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initLogger();
    }

    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)// (Optional) Whether to show thread info or not. Default true
                .logStrategy(new LogCatStrategy ())// (Optional) Changes the log strategy to print out. Default LogCat
                .methodCount(0)// (Optional) How many method line to show. Default 2
                .methodOffset(7) // (Optional) Hides internal method calls up to offset. Default 5
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    public static Context getContext() {
        return mApplication;
    }


}
