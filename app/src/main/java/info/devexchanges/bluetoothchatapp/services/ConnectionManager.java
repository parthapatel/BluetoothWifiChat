package info.devexchanges.bluetoothchatapp.services;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 4/21/2017.
 */

public class ConnectionManager {
    private int port;
    private List<PrintWriter> clients;

    public ConnectionManager(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void startServer(Activity context) {
        new Thread(new ServerThread(context)).start();
    }

    private void sendMessageToClients(String data) {
        for (PrintWriter os : clients) {
            os.println(data);
        }
    }

    public class ServerClientThread implements Runnable {
        private Context context;
        private Socket socket;
        private PrintWriter os;

        public ServerClientThread(Context context, Socket socket) {
            this.context = context;
            this.socket = socket;
            synchronized (clients) {
                try {
                    this.os = new PrintWriter(socket.getOutputStream(), true);
                    clients.add(os);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            String data;
            try (BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                while (true) {
                    data = is.readLine();
                    if (data == null) {
                        return;
                    }
                    sendMessageToClients(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    os.close();
                }
                if (clients != null) {
                    clients.remove(os);
                }
            }
        }
    }

    public class ServerThread implements Runnable {
        private Context context;

        public ServerThread(Activity context) {
            this.context = context;
        }

        @Override
        public void run() {
            try(ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    new Thread(new ServerClientThread(context, serverSocket.accept())).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
