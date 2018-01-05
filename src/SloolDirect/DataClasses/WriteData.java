package SloolDirect.DataClasses;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;

public final class WriteData {
    CalendarData calendarData = new CalendarData();
    WorkData workData = new WorkData();

    public WriteData(){
        try{
            if(workData.getNecessaryToWrite()){
                writeWorkData();
            }
            if(calendarData.getNecessaryToWrite()){
                writeCalendarData();
            }
        }catch (Exception e){
            //No data was able to be written
            System.out.println("Fail writing data");
            e.printStackTrace();
        }
    }

    private void writeWorkData() throws Exception{
        File file = new File("/Users/BabySloth/SloolData/toDoData.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        Element element = (Element) document.getFirstChild();
        element.setTextContent(workData.getToDoData());

        TransformerFactory factory1 = TransformerFactory.newInstance();
        Transformer transformer = factory1.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult streamResult = new StreamResult(file);
        transformer.transform(source, streamResult);
    }

    private void writeCalendarData() throws Exception{
        File file = new File("/Users/BabySloth/SloolData/CalendarData.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        Node years = document.getFirstChild();
        NodeList yearList = years.getChildNodes();

        for(int i = 0; i< yearList.getLength(); i++){
            Node eventsNode = yearList.item(i);

            if(eventsNode.getNodeName().equals("Year")){
                Element element = (Element) eventsNode;
                element.setTextContent(returnEventContext(element.getAttribute("id")));
            }
        }

        //TODO: FINISH THIS



        TransformerFactory factory1 = TransformerFactory.newInstance();
        Transformer transformer = factory1.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult streamResult = new StreamResult(file);
        transformer.transform(source, streamResult);
    }

    private String returnEventContext(String year){
        String eventReturn = "";
        HashMap<String, String> combinedMap = calendarData.getHashCombinedMap();
        for(String key : combinedMap.keySet()){
            String[] keyList = key.split(",");
            if(keyList[1].equals(year)){
                eventReturn += key + "|" + combinedMap.get(key)+"||";
            }
        }
        return eventReturn;
    }
}
