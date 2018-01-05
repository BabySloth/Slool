package SloolDirect.DataClasses;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public final class ReadData {
    public ReadData(){
        new WorkRead();
        new CalendarRead();
    }

    private class CalendarRead{
        private File file = new File("/Users/BabySloth/SloolData/CalendarData.xml");
        private Document document = getDocument(file);

        private NodeList nodeList = document.getElementsByTagName("Year");

        private String stringOfEvents = "";

        private CalendarRead(){
            for (int i = 0; i < nodeList.getLength(); i++){
                Element element = (Element) nodeList.item(i);
                stringOfEvents += element.getTextContent();
            }

            //Sends unsterilized information to class
            new CalendarData().setStringEventHashMap(stringOfEvents);
        }
    }

    private class WorkRead{
        private File file = new File("/Users/BabySloth/SloolData/toDoData.xml");
        private Document document = getDocument(file);
        private Element element = (Element) document.getFirstChild();

        private WorkRead(){

            new WorkData().setToDoData(element.getTextContent());
        }
    }

    private Document getDocument(File file){
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.normalizeDocument();
            return document;
        }catch (Exception e){
            System.out.println("Error in reading files");
            e.printStackTrace();
            return null;
        }
    }
}