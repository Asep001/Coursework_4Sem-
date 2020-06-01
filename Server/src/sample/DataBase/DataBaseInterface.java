package sample.DataBase;

public interface DataBaseInterface {
    long registrateUser(String name, String password);
    long logInUser(String name, String password);
    int regFolder(String id, String folderName, String mac, String path);
    String getFolder(String id, String mac);
}
