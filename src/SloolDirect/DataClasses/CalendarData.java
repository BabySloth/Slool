package SloolDirect.DataClasses;

import java.util.HashMap;

public final class CalendarData {
    //Unsterilized (from disk)
    private static String stringEventHashMap;
    //Sterilized
    private static HashMap<String, String> hashMapLongEvents = new HashMap<>();
    private static HashMap<String, String> hashMapShortEvents = new HashMap<>();
    private static HashMap<String, String> hashMapCombinedEvents = new HashMap<>();

    private static boolean necessaryToWrite = false;

    private void sterilize() {
        //Example: 241,2017|WNoSchool||360,2017,365,2017|WWinterBreak
        String[] arrayEvents = stringEventHashMap.split("\\|\\|");
        for (int i = 0; i < arrayEvents.length; i++) {
            //Example: 241,2017|WNoSchool
            String[] daysEvent = arrayEvents[i].split("\\|");

            //Determines if event last multiple days or a single day
            //and put the put the information to the corresponding date
            if (daysEvent[0].split(",").length == 4) {
                hashMapLongEvents.put(daysEvent[0], daysEvent[1]);
            } else {
                hashMapShortEvents.put(daysEvent[0], daysEvent[1]);
            }
        }
    }

    /**
     * Setters
     */

    public void setStringEventHashMap(String stringEventHashMap) {
        this.stringEventHashMap = stringEventHashMap;

        //Fixes data to be used in other classes
        sterilize();
    }

    public void setNecessaryToWrite(HashMap<String, String> hashMapShortEvents,
                                    HashMap<String, String> hashMapLongEvents){
        this.hashMapShortEvents = hashMapShortEvents;
        this.hashMapLongEvents = hashMapLongEvents;

        //Updates combined map for ease of writing to disk
        hashMapCombinedEvents.putAll(hashMapShortEvents);
        hashMapCombinedEvents.putAll(hashMapLongEvents);

        //Tells shut down to write this on to disk
        necessaryToWrite = true;
    }

    /**
     * Getters
     */
    public HashMap<String, String> getHashMapShortEvents() {
        return hashMapShortEvents;
    }

    public HashMap<String, String> getHashMapLongEvents() {
        return hashMapLongEvents;
    }

    public boolean getNecessaryToWrite(){
        return necessaryToWrite;
    }

    public HashMap<String, String> getHashCombinedMap(){
        return hashMapCombinedEvents;
    }
}
