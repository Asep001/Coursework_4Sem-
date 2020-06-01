package sample;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import sample.DataBase.DataBaseServecies;
import sample.DataBase.DataBaseInterface;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class requestHandle implements com.sun.net.httpserver.HttpHandler {

    FileTreeProcessServices fileTreeProcessServices = new FileTreeProcessServices();
    ArrayList<UploadFileInfo> uploadFiles = new  ArrayList<>();
    final String PATH_TO_STORAGE = "./storage/";

    DataBaseInterface dataBaseServices = new DataBaseServecies();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requesParams;
        String requestMethod = httpExchange.getRequestMethod();
        System.out.println(requestMethod);
        requesParams = GetRequestParams(httpExchange);
        System.out.println(requesParams);
        switch (requestMethod) {
            case "GET" : handleGETRequest(httpExchange, requesParams);
                break;
            case "POST" :{
                if (requesParams.equals(""))
                    break;
                if (requesParams.equals("singup") || requesParams.equals("login")) {
                    handleRegistration(httpExchange, requesParams);
                    break;
                }
                if (requesParams.equals("regFolder")) {
                    handleRegFolderRequest(httpExchange);
                    break;
                }
                if (requesParams.equals("isRefreshNeed")) {
                    handleIsRefreshNeedRequest(httpExchange);
                    break;
                }
                if (requesParams.equals("refresh")) {
                    handleRefreshRequest(httpExchange);
                    break;
                }
                    handlePostRequest(httpExchange, requesParams);
                }
                break;
            case "HEAD" : handleHeadRequest(httpExchange, requesParams);
                break;
            case "DELETE" : handleDeleteRequest(httpExchange, requesParams);
                break;
        }



    }

    private String GetRequestParams(HttpExchange httpExchange) {
        return httpExchange.getRequestURI().toString().replaceFirst("/","");
    }

    private File searchFileByID(String  filePath, String clientId){
        String pathToFile = PATH_TO_STORAGE + clientId + "/" + filePath;
        System.out.println(pathToFile);
        return new File(pathToFile);
    }

    private void handleGETRequest(HttpExchange httpExchange, String fileId)  throws  IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        byte[] buf = new byte[64*1024];

        Headers headers = httpExchange.getRequestHeaders();
        String clientId = headers.get("id").get(0);

        String filePath = null;
        for(UploadFileInfo item : uploadFiles){
            if (item.getClientId().equals(clientId) && item.getFileId().equals(fileId))
                filePath = item.getPath();
        }
        File file = searchFileByID(filePath, clientId);
        if (!file.exists()) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND,-1);
        } else {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, file.length());

            FileInputStream fileInputStream = new FileInputStream(file.getPath());
            int bytesCount = 1;
            while (bytesCount > 0) {
                bytesCount = fileInputStream.read(buf);
                if (bytesCount > 0) outputStream.write(buf, 0, bytesCount);
            }
            fileInputStream.close();
        }
        outputStream.flush();
        outputStream.close();
    }


    private void handleHeadRequest(HttpExchange httpExchange, String requestParamValue)  throws  IOException {
        Headers headers = httpExchange.getRequestHeaders();
        String clientId = headers.get("id").get(0);

        File file = searchFileByID(requestParamValue, clientId);

        if (!file.exists()) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND,-1);
        } else {

            httpExchange.getResponseHeaders().add("Content-Length",Long.toString(file.length()));
            httpExchange.getResponseHeaders().add("File Name",file.getName());
            httpExchange.getResponseHeaders().add("File ID",Long.toString(file.getName().hashCode()));

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, -1);
        }
    }

    private void handleDeleteRequest(HttpExchange httpExchange, String fileId) throws IOException {
        Headers headers = httpExchange.getRequestHeaders();
        String clientId = headers.get("id").get(0);
        String fileName = URLDecoder.decode(headers.get("filename").get(0), StandardCharsets.UTF_8);
        String filePath = URLDecoder.decode(headers.get("filepath").get(0), StandardCharsets.UTF_8);
        String absolutePath = URLDecoder.decode(headers.get("absolutepath").get(0), StandardCharsets.UTF_8);

        File file = searchFileByID(filePath, clientId);

        if (!file.exists()) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND,-1);
        } else {
            if (file.delete()) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, -1);
                removeFromUploadFiles(new UploadFileInfo(clientId, fileName, filePath, absolutePath, fileId));
            }
            else
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND,-1);
        }
    }

    private void removeFromUploadFiles(UploadFileInfo uploadFileInfo){
        uploadFiles.removeIf(item -> item == uploadFileInfo);
    }

    private void handlePostRequest(HttpExchange httpExchange, String fileId)  {
        Headers headers = httpExchange.getRequestHeaders();
        String clientId = headers.get("id").get(0);
        String fileName = URLDecoder.decode(headers.get("filename").get(0), StandardCharsets.UTF_8);
        String filePath = URLDecoder.decode(headers.get("filepath").get(0), StandardCharsets.UTF_8);
        String absolutePath = URLDecoder.decode(headers.get("absolutepath").get(0), StandardCharsets.UTF_8);

        String serverFilePath = createNewFolders(filePath, clientId);

        File file = new File(serverFilePath);

        byte[] fileContent;
        try {
            fileContent = httpExchange.getRequestBody().readAllBytes();
            int responseCode = saveFile(file,fileContent);
            uploadFiles.add(new UploadFileInfo(clientId, fileName, filePath, absolutePath, fileId));

            httpExchange.sendResponseHeaders(responseCode, fileId.length());

            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(fileId.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createNewFolders(String filePath, String clientId){
        String folders = PATH_TO_STORAGE + clientId + "/";
        if (filePath == null)
            return folders;
        int i;
        String[] arrOfDir = filePath.split("/");
        for (i = 0; i<arrOfDir.length-1; i++){
            try {
                folders +=(arrOfDir[i] + "/");
                Files.createDirectories(Paths.get(folders));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return folders+arrOfDir[arrOfDir.length-1];
    }

    private void handleRegistration(HttpExchange httpExchange, String requestParamValue){
        long length = 0;
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
        OutputStream outputStream = httpExchange.getResponseBody();

        Headers headers = httpExchange.getRequestHeaders();
        String clientName = headers.get("name").get(0);
        String clientPassword = headers.get("password").get(0);

        String clientId = "-1";

        if (requestParamValue.equals("login")){
           clientId = Long.toString(dataBaseServices.logInUser(clientName, clientPassword));
        }
        if (requestParamValue.equals("singup")){
            clientId = Long.toString(dataBaseServices.registrateUser(clientName, clientPassword));
            length = clientId.length();
            try {
                byteArrayInputStream.write(clientId.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int responseCode;
        if (clientId.equals("-1"))
            responseCode = HttpURLConnection.HTTP_BAD_REQUEST;
        else
            responseCode = HttpURLConnection.HTTP_OK;
        createNewFolders( null,clientId);
        try {
            httpExchange.getResponseHeaders().add("id", clientId);

            httpExchange.sendResponseHeaders(responseCode, length);

            outputStream.write(byteArrayInputStream.toByteArray());
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRegFolderRequest(HttpExchange httpExchange)  {
        try {
            Headers headers = httpExchange.getRequestHeaders();
            String id = headers.get("id").get(0);
            String folderName = headers.get("foldername").get(0);
            String mac = headers.get("mac").get(0);
            String path = headers.get("path").get(0);

            int responseCode = dataBaseServices.regFolder(id, folderName, mac, path);

            httpExchange.sendResponseHeaders(responseCode, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRefreshRequest(HttpExchange httpExchange){
        OutputStream outputStream = httpExchange.getResponseBody();
        Headers headers = httpExchange.getRequestHeaders();
        String clientId = headers.get("id").get(0);

        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();

        File file;

        long length;

        if (!clientId.equals("-1")) {
            makeXMLFile(PATH_TO_STORAGE + clientId, PATH_TO_STORAGE + clientId + "/output.xml", clientId);
            file = new File(PATH_TO_STORAGE + clientId + "/output.xml");
            length = file.length();
            int responseCode = HttpURLConnection.HTTP_OK;
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byteArrayInputStream.write(fileInputStream.readAllBytes());
                httpExchange.sendResponseHeaders(responseCode, length);
                outputStream.write(byteArrayInputStream.toByteArray());
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleIsRefreshNeedRequest(HttpExchange httpExchange){
        try {
            Headers headers = httpExchange.getRequestHeaders();
            String id = headers.get("id").get(0);
            String mac = headers.get("mac").get(0);
            String path = dataBaseServices.getFolder(id, mac);

            path = path == null ? "-1" : path;

            int responseCode = path.equals("-1") ? HttpURLConnection.HTTP_NOT_FOUND : HttpURLConnection.HTTP_OK;

            httpExchange.getResponseHeaders().add("path", path);

            httpExchange.sendResponseHeaders(responseCode, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeXMLFile(String path, String name, String clientId){
        File dir = new File(path);
        String folderName = Objects.requireNonNull(dir.listFiles())[0].getName();
        fileTreeProcessServices.makeDirectoryTree(Paths.get(path + "/" + folderName), name , clientId, uploadFiles);
    }


    private int saveFile(File file, byte[] buff) throws IOException {
        if (!file.exists()) {
            if (file.createNewFile()){
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(buff);
                fileOutputStream.close();
                return HttpURLConnection.HTTP_OK;
            }
        } else {
            return HttpURLConnection.HTTP_ACCEPTED;
        }
        return 0;
    }



}
