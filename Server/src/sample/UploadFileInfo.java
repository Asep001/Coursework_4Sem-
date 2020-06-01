package sample;

public class UploadFileInfo {
    private String clientId;
    private String fileName;
    private String path;
    private String absolutePath;
    private String fileId;

    UploadFileInfo(String clientId, String fileName, String path, String absolutePath, String fileId){
        this.clientId = clientId;
        this.fileName = fileName;
        this.path = path;
        this.absolutePath = absolutePath;
        this.fileId = fileId;
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public String getClientId() {
        return clientId;
    }

    public String getFileId() {
        return fileId;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
