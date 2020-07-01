package ru.pamishenko;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import ru.pamishenko.client.LogInDialog;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller implements Initializable {

    public TextArea chatText;
    public TextField messageToSend;
    public Button buttonSend;
    public AnchorPane upperPan;
    public ListView usersList;

    final String IP = "localhost";
    final int PORT = 8189;
    File file = new File("./4FSB.txt");
    Socket clientSocket;
    DataInputStream in;
    DataOutputStream out;
    FileInputStream fileInputStream;
    FileOutputStream fileOutputStream;

    private AtomicBoolean isAuthorised = new AtomicBoolean();

    public Controller() throws FileNotFoundException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/homeScene.fxml"));
        Controller controller = loader.getController();

    }

    @FXML
    public void exitButtonClicked(ActionEvent actionEvent) {

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setAuthorised(false);
        try {
            clientSocket = new Socket(IP, PORT);
            tryToAuth();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread timer = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isAuthorised.get()) {
                    try {
                        clientSocket.close();
                        System.out.println("Socket closed by timeout");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Socket closing failed");
                    }
                }
            }
        });
        timer.setDaemon(true);
        timer.start();

//        List<String> history = fileReaderLog();
//        for (String h : history) {
//            chatText.appendText(h);
//            System.out.println(h);
//        }
    }
    public void setAuthorised(boolean isAuthorised) {
        this.isAuthorised.set(isAuthorised);

        if(!isAuthorised){
            upperPan.setVisible(true);
            upperPan.setManaged(false);
            chatText.setVisible(true);
            usersList.setManaged(true);

            List<String> history = fileReaderLog();
            for (String h : history) {
                chatText.appendText(h + "\n");
            }
        } else {
            upperPan.setVisible(true);
            upperPan.setManaged(true);
            chatText.setVisible(true);
            usersList.setManaged(true);


//            messageToSend.setFocusTraversable(true);  // Нужно установить курсор в поле для ввода
        }
    }


    public void tryToAuth(){
        connect();
        try {
            LogInDialog ld = new LogInDialog();
            ld.authoriseInit();

            out.writeUTF("/auth " + ld.getUsername() + " " + ld.getPassword());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            fileOutputStream = new FileOutputStream(file,true);

            setAuthorised(false);

            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(true){
                            String msg = in.readUTF();
                            if(msg.equals("/authOK")){
                                setAuthorised(true);
                                break;
                            } else{
                                chatText.appendText(msg);

                            }
                        }

                        while (true) {
                            String msg = in.readUTF();
                            if(msg.startsWith("/")){
                                if(msg.startsWith("/clientList ")){
                                    String [] tokens = msg.split(" ");

                                    Platform.runLater(() -> {
                                        usersList.getItems().clear();
                                        for(int i = 1; i < tokens.length; i++) {
                                            usersList.getItems().add(tokens[i]);
                                        }
                                    });
                                }
                                if(msg.equals("/session finished")) break;
                            } else {chatText.appendText(msg + "\n");
                                fileWriteLog(msg);


                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            clientSocket.close();
                            System.out.println("Socket closed by client");
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Socket closing failed");
                        }
                        setAuthorised(false);
                    }
                }
            });
            t1.setDaemon(true);
            t1.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(messageToSend.getText()); //for private msg use "/m userName text" construction
            messageToSend.clear();
            messageToSend.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fileWriteLog(String s){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(s.getBytes());
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public  List<String> fileReaderLog(){
        List<String> history = new ArrayList<>();
        List<String> result = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()){
                history.add(bufferedReader.readLine());
            }
            if (history.size() > 100){
                for (int i = history.size()-100; i < history.size(); i++){
                    result.add(history.get(i));
                }
            }

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
