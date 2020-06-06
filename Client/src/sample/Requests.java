package sample;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


public class Requests {
    private final HttpClient client;
    private final String SERVER_URL;

    Requests(String serverIp, String serverPort){
        client = HttpClient.newHttpClient();
        SERVER_URL = "http://" + serverIp + ":" + serverPort + "/";
    }

    public byte[] sendGetRequest(String fileId, String clientId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL+fileId))
                .header("id", clientId)
                .build();

        HttpResponse<byte[]> response =
                client.send(request,HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            return null;
        }
        return response.body();
    }

    public String[] sendHeadRequest(String fileID, String clientId) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder (URI.create (SERVER_URL+fileID))
                .method ("HEAD", HttpRequest.BodyPublishers.noBody ())
                .header("id", clientId)
                .build ();

        HttpResponse <String> response = client.send (request,
                HttpResponse.BodyHandlers.ofString());

        HttpHeaders headers = response.headers();



        String[] fileInfo = new String[2];
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            fileInfo[0] = headers.allValues("name").get(0);
            fileInfo[1] = headers.allValues("content-length").get(0);
        }
        else
            fileInfo[0] = "Файл отсутствует";

        return fileInfo;
    }

    public int deleteFile(String filePath, String clientId, String fileName, long fileId, String absolutePath) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder (URI.create (SERVER_URL+fileId))
                .DELETE()
                .header("id",clientId)
                .header("filename", URLEncoder.encode(fileName, StandardCharsets.UTF_8))
                .header("filepath", URLEncoder.encode(filePath, StandardCharsets.UTF_8))
                .header("absolutepath", URLEncoder.encode(absolutePath, StandardCharsets.UTF_8))
                .build ();

        HttpResponse <Void> response = client.send (request,
                HttpResponse.BodyHandlers.discarding ());
        return response.statusCode();
    }

    public void sendPostRequest(byte[] data, String filePath, String clientId, String fileName, long fileId, String absolutePath) throws IOException, InterruptedException{

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL+fileId))
                .POST(HttpRequest.BodyPublishers.ofByteArray(data))
                .header("id",clientId)
                .header("filename", URLEncoder.encode(fileName, StandardCharsets.UTF_8))
                .header("filepath", URLEncoder.encode(filePath, StandardCharsets.UTF_8))
                .header("absolutepath", URLEncoder.encode(absolutePath, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response =
                client.send(request,HttpResponse.BodyHandlers.ofString());

        String responseInfo = response.body();

        if (response.statusCode() == HttpURLConnection.HTTP_ACCEPTED) {
            System.out.println(" файл уж есть");
        }
    }

    public long logInRequest(String name, String password) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "login"))
                .header("name", name)
                .header("password", password)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<Void> response =
                client.send(request,HttpResponse.BodyHandlers.discarding());

        String clientId = response.headers().allValues("id").get(0);

        return Long.parseLong(clientId);
    }



    public long singUpRequest(String name, String password) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "singup"))
                .header("name", name)
                .header("password", password)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response =
                client.send(request,HttpResponse.BodyHandlers.ofString());

        String clientId = response.body();

        return Long.parseLong(clientId);
    }

    public byte[] refreshRequest(String clientId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "refresh"))
                .header("id", clientId)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<byte[]> response =
                client.send(request,HttpResponse.BodyHandlers.ofByteArray());

        return response.body();
    }

    public void setFolderAndMac(long id, String folderName, String pathToFolder, String macAddress) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "regFolder"))
                .header("id", String.valueOf(id))
                .header("folderName", folderName)
                .header("mac", macAddress)
                .header("path", pathToFolder)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<Void> response =
                client.send(request,HttpResponse.BodyHandlers.discarding());
    }

    public String isRefreshNeedRequest(long id, String macAddress) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "isRefreshNeed"))
                .header("id", String.valueOf(id))
                .header("mac", macAddress)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<Void> response =
                client.send(request,HttpResponse.BodyHandlers.discarding());

        String folderPath = response.headers().allValues("path").get(0);

        return folderPath;
    }
}
