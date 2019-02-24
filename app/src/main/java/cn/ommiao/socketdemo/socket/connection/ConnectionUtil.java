package cn.ommiao.socketdemo.socket.connection;

import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import cn.ommiao.socketdemo.BaseActivity;
import cn.ommiao.socketdemo.socket.Config;

public class ConnectionUtil {

    private Object lock = new Object();

    private Socket socket;

    private BaseActivity context;

    private String ip;
    private int port;

    private Client client;

    public ConnectionUtil create(BaseActivity context){
        this.context = context;
        return this;
    }

    public ConnectionUtil ip(String ip){
        this.ip = ip;
        return this;
    }

    public ConnectionUtil port(int port){
        this.port = port;
        return this;
    }

    public void connet(ConnectionListener listener){
        new Thread(() -> {
            socket = new Socket();
            if(socket.isConnected()){
                client = new Client(socket);
            }
        }).start();
    }

    public void send(String msg){
        new Thread(() -> {
            try {
                Socket socket = new Socket(ip, port);
                OutputStream os = socket.getOutputStream();
                os.write(msg.getBytes());
                os.flush();
                socket.shutdownOutput();
                InputStream is = socket.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader bufReader = new BufferedReader(reader);
                String s;
                final StringBuffer sb = new StringBuffer();
                while ((s = bufReader.readLine()) != null){
                    Logger.d(s);
                    sb.append(s);
                    if(s.contains(Config.END)){
                        break;
                    }
                }
                //...
                bufReader.close();
                reader.close();
                is.close();
                os.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public interface ConnectionListener{

        void onConnected();
        void onConnectFail();

    }

}
