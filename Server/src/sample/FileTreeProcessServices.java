package sample;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileTreeProcessServices {

    private ArrayList<UploadFileInfo> fileList;

    final String PATH_TO_STORAGE = "./storage/";

    public void makeDirectoryTree(final Path path, String filename, String clientId, ArrayList<UploadFileInfo> fileList)  {
        try {
            this.fileList = fileList;
            final Document document = createXmlDocument(path, clientId);

            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            final DOMSource source = new DOMSource(document);

            final StreamResult streamResult;
            streamResult = new StreamResult(new FileWriter(filename));
            transformer.transform(source, streamResult);
        } catch (IOException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

    }


    private Node processFile(final Document document, final Path path, String clientId) {
        final Element xmlElement = document.createElement("file");
        long timestamp = path.toFile().lastModified();

        String fileName = path.getFileName().toString();
        String filePath = path.toString().replaceAll("\\\\","/");
        String absolutePath = path.toAbsolutePath().toString().replaceAll("\\\\","/");
        filePath = filePath.replaceFirst(PATH_TO_STORAGE + clientId + "/", "");
        String fileId = "0";
        for (UploadFileInfo item : fileList){
            if (item.getPath().equals(filePath) && item.getFileName().equals(fileName) && item.getClientId().equals(clientId))
                fileId = item.getFileId();
        }

        xmlElement.setAttribute("name", fileName);
        xmlElement.setAttribute("path", filePath);
        xmlElement.setAttribute("lastModified", Long.toString(timestamp));
        xmlElement.setAttribute("id", fileId);
        xmlElement.setAttribute("absolutepath", absolutePath);


        return xmlElement;
    }

    public Node processFolder(final Document document, final Path path, String clientId) throws IOException {
        final Element xmlElement = document.createElement("directory");
        xmlElement.setAttribute("name", path.getFileName().toString());
        xmlElement.setAttribute("path", path.toAbsolutePath().toString());
        try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (final Path pathElement : directoryStream) {
                if (Files.isDirectory(pathElement)) {
                    xmlElement.appendChild(processFolder(document, pathElement, clientId));
                } else {
                    xmlElement.appendChild(processFile(document, pathElement, clientId));
                }
            }
        }
        return xmlElement;
    }

    public  Document createXmlDocument(final Path path, String  clientId) throws ParserConfigurationException, IOException {
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        document.appendChild(processFolder(document, path, clientId));
        return document;
    }
}
