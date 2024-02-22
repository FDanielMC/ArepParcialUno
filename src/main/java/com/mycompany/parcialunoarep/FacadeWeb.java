/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.parcialunoarep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author daniel.moreno-c
 */
public class FacadeWeb {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:45000/consulta?";

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
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
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine = in.readLine();
            String outputLine = "";
            if (inputLine != null) {
                System.out.println("Recibí: " + inputLine);
                URI uri = new URI(inputLine.split(" ")[1]);
                String path = uri.getPath();
                String query = uri.getQuery();
                if (path.startsWith("/consulta")) {
                    outputLine = restService(query);
                } else if (path.startsWith("/cliente") || path.startsWith("/")) {
                    outputLine = clientWeb();
                }

                out.println(outputLine);
            }
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    public static String clientWeb() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "    <head>\n"
                + "        <title>Reflective ChatGPT</title>\n"
                + "        <meta charset=\"UTF-8\">\n"
                + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "    </head>\n"
                + "    <body>\n"
                + "        <h1>Reflective ChatGPT</h1>\n"
                + "        <form action=\"/consulta\">\n"
                + "            <label for=\"name\">Name:</label><br>\n"
                + "            <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>\n"
                + "            <input type=\"button\" value=\"Calcular\" onclick=\"loadGetMsg()\">\n"
                + "        </form> \n"
                + "        <h3>Respuesta</h3>\n"
                + "        <div id=\"getrespmsg\"></div>\n"
                + "\n"
                + jsClient()
                + "    </body>\n"
                + "</html>\n";
    }

    public static String jsClient() {
        return "<script>\n"
                + "            function loadGetMsg() {\n"
                + "                let nameVar = document.getElementById(\"name\").value;\n"
                + "                const xhttp = new XMLHttpRequest();\n"
                + "                xhttp.onload = function() {\n"
                + "                    document.getElementById(\"getrespmsg\").innerHTML =\n"
                + "                    this.responseText;\n"
                + "                }\n"
                + "                xhttp.open(\"GET\", \"/consulta?comando=\"+nameVar);\n"
                + "                xhttp.send();\n"
                + "            }\n"
                + "        </script>";
    }

    public static String restService(String query) throws IOException {
        String outputLine = "";
        URL obj = new URL(GET_URL + query);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader inC = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLineC;
            StringBuffer response = new StringBuffer();
            while ((inputLineC = inC.readLine()) != null) {
                response.append(inputLineC);
            }
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n" + response;
            inC.close();
        } else {
            System.out.println("GET request not worked");
        }
        System.out.println("GET DONE");
        return outputLine;
    }

}
