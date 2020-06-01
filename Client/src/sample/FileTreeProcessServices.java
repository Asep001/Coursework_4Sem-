package sample;

import javafx.scene.control.TreeItem;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class FileTreeProcessServices {

    ArrayList<FileInfo> fileList = new ArrayList<>();

    public TreeItem<FileInfo> getTreeFromFile(String path) {
        try {
            File file = new File(path);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            String element = "directory";
            NodeList matchedElementsList = document.getElementsByTagName(element);
            Node node = matchedElementsList.item(0);
            return parseXml(node);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TreeItem<FileInfo> parseXml(Node element) {
        TreeItem<FileInfo> root = new TreeItem<>();
        root.setValue(new FileInfo(element.getAttributes().item(0).getNodeValue(),"-1"));
        root.setExpanded(true);
        if (!element.hasChildNodes())
            return root;
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);

            if (node.getNodeType() == Node.TEXT_NODE)
                continue;
            NamedNodeMap attributes = node.getAttributes();
            if (node.hasChildNodes())
                root.getChildren().add(parseXml(node));
            else {
                int attributesCount = 0;
                if (attributes.getLength() > 2)
                    attributesCount = 3;
                root.getChildren().add(new TreeItem<>(new FileInfo(attributes.item(attributesCount).getNodeValue(), attributes.item(1).getNodeValue())));
            }
        }
        return root;
    }

    public ArrayList<FileInfo> getTreeFromFileToArray(String path) {
        try {
            File file = new File(path);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            String element = "directory";
            NodeList matchedElementsList = document.getElementsByTagName(element);
            Node node = matchedElementsList.item(0);
            parseXmlToArray(node);
            return fileList;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void parseXmlToArray(Node element) {
        if (!element.hasChildNodes())
            return ;
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);

            if (node.getNodeType() == Node.TEXT_NODE)
                continue;
            NamedNodeMap attributes = node.getAttributes();
            if (node.hasChildNodes())
                parseXmlToArray(node);
            else {
                int attributesCount = 0;
                if (attributes.getLength() > 2)
                    attributesCount = 3;
                String filePath = attributes.item(4).getNodeValue();
                String absolurePath = attributes.item(0).getNodeValue();

                long lastModified = Long.parseLong(attributes.item(2).getNodeValue());
                fileList.add(new FileInfo(attributes.item(attributesCount).getNodeValue(),
                        attributes.item(1).getNodeValue()).setFilePath(filePath).setLastModified(lastModified).setAbsolutePath(absolurePath));
            }
        }
    }
}
