package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;



public class Requests {
    private HttpClient client;

    final String SERVER_URL = "http://localhost:8001/";

    Requests(){
        client = HttpClient.newHttpClient();
    }

    public byte[] sendGetRequest(String fileId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL+fileId))
                .build();

        HttpResponse<byte[]> response =
                client.send(request,HttpResponse.BodyHandlers.ofByteArray());


        if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            return null;
        }
        return response.body();
    }

    public String[] sendHeadRequest(Long fileID) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder (URI.create (SERVER_URL+fileID))
                .method ("HEAD", HttpRequest.BodyPublishers.noBody ())
                .build ();

        HttpResponse <String> response = client.send (request,
                HttpResponse.BodyHandlers.ofString());

        HttpHeaders headers = response.headers();

        String[] fileName = new String[2];
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            fileName[0] = headers.allValues("file name").get(0);
            fileName[1] = headers.allValues("content-length").get(0);
        }
        else
            fileName[0] = "Файл отсутствует";

        return fileName;
    }

    public int sendDeleteRequest(Long fileID) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder (URI.create (SERVER_URL+fileID))
                .method ("DELETE", HttpRequest.BodyPublishers.noBody ())
                .build ();

        HttpResponse <Void> response = client.send (request,
                HttpResponse.BodyHandlers.discarding ());
        return response.statusCode();
    }

    public Long sendPostReuest(byte[] data, String clientId) throws IOException, InterruptedException{
        //File file = new File(filePath);

        //FileInputStream fileInputStream = new FileInputStream(file);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL+clientId))
                .POST(HttpRequest.BodyPublishers.ofByteArray(data))
                .build();

        HttpResponse<String> response =
                client.send(request,HttpResponse.BodyHandlers.ofString());

        String fileName = response.body();

        if (response.statusCode() == HttpURLConnection.HTTP_ACCEPTED) {
            System.out.println(" файл уж есть");
        }
        return Long.valueOf(fileName);
    }

    public long logInRequest(String name, String password) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "login"))
                .header("name", name)
                .header("password", password)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response =
                client.send(request,HttpResponse.BodyHandlers.ofString());

        String fileName = response.body();

        return Long.parseLong(fileName);
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

        String fileName = response.body();

        return Long.parseLong(fileName);
    }
}
