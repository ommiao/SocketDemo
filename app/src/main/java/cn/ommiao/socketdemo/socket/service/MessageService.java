package cn.ommiao.socketdemo.socket.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;

import cn.ommiao.socketdemo.socket.Config;
import cn.ommiao.socketdemo.socket.message.ActionDefine;
import cn.ommiao.socketdemo.socket.message.heartbeat.HeartBeatWrapper;
import cn.ommiao.socketdemo.socket.message.base.MessageBase;

public class MessageService extends Service {

    private static final long HEART_BEAT_RATE = 3 * 1000;

    public static String userCode = UUID.randomUUID().toString();

    private long sendTime = 0L;

    private WeakReference<Socket> mSocket;
    private ReadThread mReadThread;

    private static HeartBeatWrapper HEART_BEAT_WRAPPER;

    private LocalBroadcastManager mLocalBroadcastManager;

    private int retryTime = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iMessageService;
    }

    private IMessageService.Stub iMessageService = new IMessageService.Stub(){

        @Override
        public boolean sendMessage(String message) {
            return sendMsg(message);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initHeartBeatData();
        startSocket();
        initLocalBroadcast();

    }

    private void initHeartBeatData() {
        HEART_BEAT_WRAPPER = new HeartBeatWrapper().action(ActionDefine.ACTION_HEART_BEAT);
        Logger.d(HEART_BEAT_WRAPPER.getStringMessage());
    }


    private void startSocket() {
        new InitSocketThread().start();
    }

    private void initLocalBroadcast() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {

        @Override
        public void run() {
            if(needHeartBeat()){
                boolean success = sendMsg(HEART_BEAT_WRAPPER.getStringMessage());
                if(!success){
                    retryTime++;
                    if(retryTime == 3){
                        Intent intent = new Intent(ActionDefine.ACTION_DISCONNECTED);
                        mLocalBroadcastManager.sendBroadcast(intent);
                    }
                    mHandler.removeCallbacks(heartBeatRunnable);
                    mReadThread.release();
                    releaseSocket(mSocket);
                    new InitSocketThread().start();
                }
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }

        private boolean needHeartBeat(){
            return System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE;
        }
    };

    public boolean sendMsg(String message){
        if(mSocket == null || mSocket.get() == null){
            return false;
        }
        Socket socket = mSocket.get();
        if(!socket.isClosed() && !socket.isOutputShutdown()){
            new Thread(() -> {
                try {
                    OutputStream os = socket.getOutputStream();
                    os.write(message.getBytes());
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            sendTime = System.currentTimeMillis();
            Logger.d("Message Send: " + message + ".");
        } else {
            return false;
        }
        return true;
    }

    private void initSocket() throws IOException {
        Socket socket = new Socket(Config.IP, Config.PORT);
        mSocket = new WeakReference<>(socket);
        mReadThread = new ReadThread(socket);
        mReadThread.start();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
    }

    private void releaseSocket(WeakReference<Socket> mSocket){
        try {
            if(mSocket != null){
                Socket socket = mSocket.get();
                if(socket != null && !socket.isClosed()){
                    socket.close();
                }
                socket = null;
                mSocket = null;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    class InitSocketThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                initSocket();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public class ReadThread extends Thread{

        private WeakReference<Socket> mWeakSocket;
        private boolean isStart = true;

        ReadThread(Socket socket) {
            mWeakSocket = new WeakReference<>(socket);
        }

        void release(){
            isStart = false;
            releaseSocket(mWeakSocket);
        }

        @Override
        public void run() {
            super.run();
            Socket socket = mWeakSocket.get();
            if(socket != null){
                try {
                    InputStream is = socket.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int length;
                    while (!socket.isClosed() && !socket.isInputShutdown()
                            && isStart && ((length = is.read(buffer)) != -1)){
                        if(length > 0){
                            String message = new String(Arrays.copyOf(buffer, length)).trim();
                            Logger.d("Reveived Message: " + message);
                            MessageBase base = MessageBase.fromJson(message, MessageBase.class);
                            String action = base.getAction();
                            if(ActionDefine.ACTION_HEART_BEAT.equals(action)){
                                retryTime = 0;
                            }
                            Intent intent = new Intent(action);
                            intent.putExtra("message", message);
                            mLocalBroadcastManager.sendBroadcast(intent);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mHandler.removeCallbacks(heartBeatRunnable);
        mReadThread.release();
        releaseSocket(mSocket);
        return true;
    }
}
