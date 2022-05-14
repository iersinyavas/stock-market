package com.artsoft.stock.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ShareOrderClient {

    public static void main(String[] args) {

        Socket serverSocket;
        DataOutputStream dataOutputStream;
        try {
            serverSocket = new Socket("localhost", 1453);
            dataOutputStream = new DataOutputStream(serverSocket.getOutputStream());
            dataOutputStream.writeUTF("fghfjtyujyıuou");
            serverSocket.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

}