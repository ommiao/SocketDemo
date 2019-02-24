package cn.ommiao.socketdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.Arrays;

import cn.ommiao.socketdemo.socket.Config;

public class MessageService extends Service {

    private static final long HEART_BEAT_RATE = 3 * 1000;

    private long sendTime = 0L;

    private WeakReference<Socket> mSocket;
    private ReadThread mReadThread;


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
        new InitSocketThread().start();
    }

    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {

        @Override
        public void run() {
            if(needHeartBeat()){
                boolean success = sendMsg("@heartbeat");
                if(!success){
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
        try {
            if(!socket.isClosed() && !socket.isOutputShutdown()){
                OutputStream os = socket.getOutputStream();
                os.write(message.getBytes());
                os.flush();
                sendTime = System.currentTimeMillis();
                Logger.d("Message Send Success at " + sendTime + ".");
            } else {
                return false;
            }
        } catch (IOException e){
            e.printStackTrace();
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
                if(!socket.isClosed()){
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

        public void release(){
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
                            String message = new String(Arrays.copyOf(buffer, length)).trim().replace(Config.END, "");
                            Logger.d("Reveived Message: " + message);
                            if("@heartbeat".equals(message)){
                                Logger.d("@heartbeat");
                            } else {

                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
