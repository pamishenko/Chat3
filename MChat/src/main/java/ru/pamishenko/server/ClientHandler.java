package ru.pamishenko.server;


import java.io.*;
import java.net.Socket;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class ClientHandler {

    private OutputStream outputStream;
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    String userName;

    public String getUserName() {
        return userName;
    }

    public ClientHandler(Server server, Socket socket){
        try {


            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true){
                        String str = in.readUTF();
                        if(str.startsWith("/auth")){
                            String [] tokens = str.split(" ");
                            String checkUserName = AuthService.getNameByLoginAndPass(tokens[1], tokens[2]);
                            System.out.println(checkUserName);
                            if(checkUserName!=null){
                                if(!server.isUserConnected(checkUserName)){
                                    userName = checkUserName;
                                    System.out.println(checkUserName + " connected");
                                    out.writeUTF("/authOK");
                                    server.subscribe(ClientHandler.this);
                                    server.broadcastMsg("Администратор: " + userName + " подключился к чату" + "\n");
                                    sendMsg("Администратор: Добро пожаловать, дорогой " + userName + "\n");
                                    break;
                                } else sendMsg("Пользователь с таким логином уже вошел в чат" + "\n");
                            } else sendMsg("Администратор: логин или пароль введены некорректно" + "\n");
                        }
                    }
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            out.writeUTF("/session finished");
                            System.out.println(userName + " disconnected");
                            break;
                        } else {
                            if (str.startsWith("/m")){
                                String [] msgParts = str.split(" ",3);
                                server.sendPrivateMsg(userName, msgParts[1], msgParts[2]);
                            } else {
                                server.broadcastMsg(userName + ": " + str);
                            }
                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Socket closing error");
                    }
                    server.unsubscribe(ClientHandler.this);
                    server.broadcastMsg("Администратор: " + userName + " покинул чат" + "\n");
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg (String msg){

      //  String time = String.format("%tc", new Date());

        try {

            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}