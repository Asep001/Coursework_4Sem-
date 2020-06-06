package sample;

import DirectoryWatcher.DirectoryWatcher;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;

public class Client {

    public volatile boolean isAlive = false;

    public String mainDirectory;
    public String MainDirectoryParent;

    public long id;
    private InetAddress ipAddress;
    private String macAddress = "";
    private final Timer timer = new Timer();
    private int  timerPeriod = 1000 * 60 * 5;
    public createWindowsService createWindowsService;
    private final Stage primaryStage;
    private Requests requests;

    private ArrayList<FileInfo> synchronizedFiles = new ArrayList<>();

    FileTreeProcessServices fileTreeProcessServices = new FileTreeProcessServices();
    final DirectoryChooser directoryChooser = new DirectoryChooser();

    Client(Stage primaryStage){
        this.primaryStage = primaryStage;
        SetIpAndMacAddress();
    }

    public void setTimerPeriod(int timerPeriod) {
        this.timerPeriod = timerPeriod;
    }

    public int getTimerPeriod() {
        return timerPeriod;
    }

    public String getMacAddress() {
        return this.macAddress;
    }


    public void openRegistration(){
        this.id = -1;
        createWindowsService.createRegistrationWindow("Здравствуйте");
    }

    public void start(String serverIp, String serverPort) {

        requests = new Requests(serverIp, serverPort);
        createWindowsService = new createWindowsService(this.primaryStage,this.requests, this);
        openRegistration();
    }

    public void loadDirectory(){
        ProgressDialog progress = null;
        File dir = directoryChooser.showDialog(this.primaryStage);
        if (dir == null)
            return;
        try {
            progress = new ProgressDialog();
            progress.showDialog();//запуск прогресс бара
            mainDirectory = dir.getPath();
            mainDirectory = mainDirectory.replaceAll("\\\\","/");
            requests.setFolderAndMac(id,dir.getName(),mainDirectory,getMacAddress());
            MainDirectoryParent = dir.getParent();
            getFilesFromDirectory(dir,MainDirectoryParent);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            progress.closeDialog();//закрытие прогрессбара
        }

    }

    private void getFilesFromDirectory(File directory, String parentDir) {
        for (File file: Objects.requireNonNull(directory.listFiles())){
            if (file.isDirectory())
                getFilesFromDirectory(file, parentDir);
            else{
                try {
                    uploadFile(file, parentDir);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    public void uploadFile(File file, String parentDir) throws IOException, InterruptedException {
        String filePath = processFilePath(file, parentDir);

        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();

        String fileName = file.getName();
        if (fileName.startsWith("~") || getFileExtension(file).equals("TMP"))
            return;

        fileInputStream = new FileInputStream(file);
        byteArrayInputStream.write(fileInputStream.readAllBytes());




        long fileId = file.getName().hashCode() + filePath.hashCode() + this.id;

        requests.sendPostRequest(byteArrayInputStream.toByteArray(),filePath, Long.toString(this.id),
                                            file.getName(), fileId, file.getAbsolutePath());

        try {
            if (fileInputStream != null)
                fileInputStream.close();
        } catch (IOException e) { e.printStackTrace(); }
            try { byteArrayInputStream.close(); } catch (IOException e) { e.printStackTrace(); }
    }

    public void closeFileWatcherThreads(File directory) {
        if (directory.listFiles() != null) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.isDirectory())
                    closeFileWatcherThreads(file);
            }
        }
        String path = directory.getAbsolutePath() + "/0";
        File file = new File(path);
        try {
            file.createNewFile();
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exitFromAcc(){
        isAlive = false;
        if (!mainDirectory.equals("-1"))
            closeFileWatcherThreads(new File(mainDirectory));
        synchronizedFiles = new ArrayList<>();
    }

    public void deleteFile(File file, String parentDir){
        String filePath = processFilePath(file, parentDir);
        try {
            long fileId = file.getName().hashCode() + filePath.hashCode() + this.id;
            int responseCode =requests.deleteFile(filePath, Long.toString(this.id), file.getName(), fileId, file.getAbsolutePath());
            if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                createWindowsService.createWindowWithLabel("Не удалось удалить файл");
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createFileList(File directory, String parentDir) {
        for (File file: Objects.requireNonNull(directory.listFiles())){
            if (file.isDirectory())
                createFileList(file, parentDir);
            else{
                createList(file, parentDir);
            }
        }
    }

    private void createList(File file, String parentDir){
        String filePath = processFilePath(file, parentDir);
        String fileName = file.getName();
        long lastModified = file.lastModified();

        synchronizedFiles.add(new FileInfo(fileName,"0").setFilePath(filePath).setLastModified(lastModified));
    }

    private String processFilePath(File file, String parentDir){
        String filePath = file.getPath().replace(parentDir, "");
        filePath = filePath.replaceAll("\\\\","/");
        if (filePath.startsWith("/")){
            filePath = filePath.replaceFirst("/","");
        }
        return filePath;
    }

    private void SetIpAndMacAddress(){
        try {
            this.ipAddress = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(this.ipAddress);
            if (networkInterface != null) {
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (int i = 0; i < mac.length; i++) {
                        this.macAddress += String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "");
                    }
                } else {
                    System.out.println("Address doesn't exist or is not accessible.");
                }
            } else {
                System.out.println("Network Interface for the specified address is not found.");
            }
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }

    }

    public void refreshFileTree() throws IOException, InterruptedException {
            saveFileTree(requests.refreshRequest(Long.toString(id)));
            setFoldersTree();
    }

    private void startSynchronization(File directory) throws IOException, InterruptedException {
        createFileList(directory, MainDirectoryParent);

        saveFileTree(requests.refreshRequest(Long.toString(id)));

        ArrayList<FileInfo> storedFiles = new ArrayList<>();

        storedFiles = fileTreeProcessServices.getTreeFromFileToArray("output.xml");

        String absolutePath;

        for(FileInfo storedFile : storedFiles) {
            boolean isInClientDir = false;
            for (FileInfo localFile : synchronizedFiles) {
                if (localFile.getFilePath().equals(storedFile.getFilePath())
                        && localFile.getFileName().equals(storedFile.getFileName())) {
                    isInClientDir = true;
                    if (localFile.getLastModified() > storedFile.getLastModified()){
                        String localMainDirectory = MainDirectoryParent;
                        localMainDirectory = localMainDirectory.replaceAll("\\\\", "/");
                        File file = new File(localMainDirectory + localFile.getFilePath());
                        deleteFile(file, MainDirectoryParent);
                        uploadFile(file, MainDirectoryParent);
                    }
                    break;
                }
            }
            if (!isInClientDir) {
                absolutePath = storedFile.getAbsolutePath();
                requests.deleteFile(storedFile.getFilePath(), Long.toString(id), storedFile.getFileName(),
                                                                Long.parseLong(storedFile.getFileId()),absolutePath);
            }
        }
        for (FileInfo localFile : synchronizedFiles){
            boolean isOnServer = false;
            for (FileInfo storedFile : storedFiles){
                if (localFile.getFilePath().equals(storedFile.getFilePath())
                        && localFile.getFileName().equals(storedFile.getFileName())) {
                    isOnServer = true;
                    break;
                }
            }
            if (!isOnServer) {
                String localMainDirectory = MainDirectoryParent;
                localMainDirectory = localMainDirectory.replaceAll("\\\\", "/");
                File file = new File(localMainDirectory + localFile.getFilePath());

                uploadFile(file, MainDirectoryParent);
            }
        }


    }

    public void initialiseLogin() throws IOException, InterruptedException {
        isAlive = true;

        mainDirectory = requests.isRefreshNeedRequest(id,macAddress);

        if (!mainDirectory.equals("-1")) {
            File directory = new File(mainDirectory);
            if (directory.exists()) {
                MainDirectoryParent = directory.getParent();

                startSynchronization(directory);

                new DirectoryWatcher(mainDirectory, this);
            }
        }
        refreshFileTree();

        setTimer(timerPeriod);
    }

    public void setTimer(int timerPeriod){
        RefreshTimerTask timerTask = new RefreshTimerTask(this);
        timer.schedule(timerTask, timerPeriod);
    }

    private void saveFileTree(byte[] buff){
        try {
            File file = new File("output.xml");
            if (!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = null;
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(buff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setFoldersTree(){
        TreeView treeView = ((TreeView) primaryStage.getScene().lookup("#foldersTreeView"));

        EventHandler<MouseEvent> mouseEventHandle = this::handleMouseClicked;

        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);

        treeView.setRoot(fileTreeProcessServices.getTreeFromFile("output.xml"));
    }
    private void handleMouseClicked(MouseEvent event) {

        if (event.getClickCount() == 2 && !event.isConsumed()) {
            event.consume();

            TreeView treeView = ((TreeView) primaryStage.getScene().lookup("#foldersTreeView"));

            Node node = event.getPickResult().getIntersectedNode();
            if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {

                TreeItem treeItem = (TreeItem) treeView.getSelectionModel().getSelectedItem();

                String fileId = ((FileInfo)treeItem.getValue()).getFileId();
                String fileName = ((FileInfo)treeItem.getValue()).getFileName();

                boolean isFolder = fileId.equals("-1");

                if(!isFolder)
                    //downloadFiles(fileId, fileName);
                    createWindowsService.createAdditionalMenuForFile(fileId, fileName);
            }
        }
    }

    public void downloadFiles(String fileId, String fileName){
        byte[] buf;
        try {
            buf = requests.sendGetRequest(fileId, Long.toString(id));
            if (buf == null) {
                createWindowsService.createWindowWithLabel("Произошла ошибка при загрузке файлов");
            }
            else {
                File dir = directoryChooser.showDialog(this.primaryStage);
                if (dir == null)
                    return;

                String path = dir.getAbsolutePath() + File.separator + fileName;
                File file = new File(path);
                if (!file.createNewFile())
                    return;
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(buf);
                fileOutputStream.close();


            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getFileInfo(String fileId, String fileName){
        String[] fileInfo;
        try {
            String infoString;
            fileInfo = requests.sendHeadRequest(fileId, Long.toString(id));
            if (!fileInfo[0].equals("Файл отсутствует"))
                infoString = "Имя файла: " + fileName + ";\nРазмер файла: " + fileInfo[1];
            else
                infoString = "Имя файла: " + fileInfo[0];
            createWindowsService.createWindowWithLabel(infoString);
            } catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setBackgroundColor(Color color){
        AnchorPane anchorPane = (AnchorPane) primaryStage.getScene().lookup("#back");
        anchorPane.setBackground(new Background(new BackgroundFill(color,null,null)));
    }

    public void setTreeColor(Color color){
        TreeView treeView = ((TreeView) primaryStage.getScene().lookup("#foldersTreeView"));
        treeView.setBackground(new Background(new BackgroundFill(color,null,null)));
    }

}
