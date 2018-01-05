package SloolDirect.DataClasses;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class WorkData {
    //Unsterilized (from disk) and to write to disk
    private static String toDoData;
    //Sterilized
    private static ObservableList<String> toDoList = FXCollections.observableArrayList();

    private static boolean necessaryToWrite = false;

    private void sterilizeData(){
        //If there is no previously stored data, just stop
        if (toDoData.isEmpty()) {
            return;
        }

        //Removes the [] in the string
        String dataSterilize = toDoData.substring(1, toDoData.length() - 1);

        //Removes "," in beginning to prevent random /n
        if(dataSterilize.startsWith(", ")){
            System.out.println(dataSterilize);
        }

        //Breaks down string into each individual to do ", "
        String[] toDoListArray = dataSterilize.split(", ");

        //Adds the events to the toDoList
        for (int i = 0; i < toDoListArray.length; i++) {
            toDoList.add(toDoListArray[i]);
        }
    }

    public void setToDoData(String toDoData){
        this.toDoData = toDoData;
        sterilizeData();
    }

    /**
     *Setters
     */

    public void setNecessaryToWrite(ObservableList<String> toDoList){
        this.toDoList = toDoList;
        necessaryToWrite = true;

        //To unsterilized
        toDoData = toDoList.toString();
    }

    /**
     *Getters
     */
    public String getToDoData(){
        return toDoData;
    }

    public ObservableList<String> getToDoList() {
        return toDoList;
    }

    public boolean getNecessaryToWrite(){
        return necessaryToWrite;
    }
}
