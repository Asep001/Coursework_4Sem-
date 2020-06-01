package sample;

public class FileInfo {
   private final String fileName;
   private final String fileId;
   private long lastModified;
   private String filePath;
   private String absolutePath;

   FileInfo(String fileName, String fileId){
       this.fileId = fileId;
       this.fileName = fileName;
   }

    public FileInfo setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public FileInfo setLastModified(long lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getLastModified() {
        return lastModified;
    }

    public FileInfo setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
        return this;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
