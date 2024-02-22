/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.parcialunoarep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 *
 * @author daniel.moreno-c
 */
public class ReflectiveChatGPT {

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(45000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean run = true;
        while (run) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine = in.readLine();
            String outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n";
            if (inputLine != null) {
                System.out.println("Recibí: " + inputLine);
                URI uri = new URI(inputLine.split(" ")[1]);
                String path = uri.getPath();
                String query = uri.getQuery();
                query = query.replace("comando=", "");
                if (path.startsWith("/consulta")) {
                    String command = query.split("\\(")[0];
                    String[] params = params(query.split("\\(")[1]);
                    System.out.println(command);
                    if (command.startsWith("Class")) {
                        String methods = declaredMethod(params);
                        String fields = declaredFields(params);
                        outputLine += "<h1>Class</h1>\n" + "<h2>Metodos:</h2>\n" + methods + "<br>\n"
                                + "<h2>Campos;</h2>\n" + fields;

                    }
                    else if(command.startsWith("Invoke")){
                        Class c = Class.forName(command);
                        
                    }

                }
                out.println(outputLine);
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    public static String[] params(String paramsString) {
        String params = paramsString.replace(")", "");
        String[] arrayStrings = params.split(",");
        return arrayStrings;
    }

    public static String declaredMethod(String[] params) throws ClassNotFoundException {
        String methods = "<h4>";
        for (int i = 0; i < params.length; i++) {
            Class c = Class.forName(params[i]);
            Member[] mbrs = c.getDeclaredMethods();
            for (Member mbr : mbrs) {
                methods += ((Method) mbr).toGenericString() + "<br>\n";
            }
        }
        return methods += "</h4>\n";
    }

    public static String declaredFields(String[] params) throws ClassNotFoundException {
        String methods = "<h4>";
        for (int i = 0; i < params.length; i++) {
            Class c = Class.forName(params[i]);
            Member[] mbrs = c.getDeclaredFields();
            for (Member mbr : mbrs) {
                methods += ((Field) mbr).toGenericString() + "<br>\n";
            }
        }
        return methods += "</h4>\n";
    }
    

}
