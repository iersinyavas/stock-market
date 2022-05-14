package com.artsoft.stock.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SwapTransaction {

    public static void main(String[] args) {

        ServerSocket serverSocket;
        Socket clientSocket;
        DataInputStream dataInputStream;
        String mesaj;
        try {
            serverSocket = new ServerSocket(1453);
            clientSocket = serverSocket.accept();
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            mesaj = dataInputStream.readUTF();
            System.out.println("Alınan mesaj: " + mesaj);
            serverSocket.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

}