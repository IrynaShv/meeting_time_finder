import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.time.Duration;
import java.util.Map;
import java.io.IOException;
import java.text.ParseException;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TimeFinder {

	public enum COLS {
		ID, 
		START_TIME,
		END_TIME
	}

	private final int COLUMN_COUNT = COLS.values().length;
	private static LinkedHashMap<Date, ArrayList<Event>> week = new LinkedHashMap<Date, ArrayList<Event>>();
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String args[]) throws ParseException {
		Date weekFromNow = getWeekFromNow();
		Date today = getToday();
    	Path csvFile = Paths.get("calendar.csv");

        int currentCol = 1;
        LocalTime startTime = null;
        LocalTime endTime = null;
        Date day  = null;

        String[] parts = null;


        try (BufferedReader br = Files.newBufferedReader(csvFile, StandardCharsets.US_ASCII)) { 	
			String line = br.readLine();
		
			while (line != null) {
				parts = line.split(",");
				day = formatter.parse(parts[COLS.START_TIME.ordinal()].trim().split(" ")[0]);
				if (day.before(weekFromNow) && day.after(today)) {
					startTime = LocalTime.parse(parts[COLS.START_TIME.ordinal()].trim().split(" ")[1]);
					endTime = LocalTime.parse(parts[COLS.END_TIME.ordinal()].trim().split(" ")[1]);
					ArrayList<Event> currentEvents = week.get(day);
					if (currentEvents != null) {
						boolean found = false;
						int insertIdx = 0;
						for (int i = 0; i < currentEvents.size(); i++) {
							if (currentEvents.get(i).contains(startTime) || currentEvents.get(i).contains(endTime) || currentEvents.get(i).isBetween(startTime, endTime)) {
								week.get(day).get(i).update(startTime, endTime);
								found = true;
							} else if (currentEvents.get(i).getEndTime().isAfter(endTime)) {
						
								insertIdx = i;
								break;
							} else {
								insertIdx = i + 1;
							}
						}
						if (!found) {
							week.get(day).add(insertIdx, new Event(startTime, endTime));	
						}
					} else {
						week.put(day, new ArrayList<Event>());
						week.get(day).add(new Event(startTime, endTime));
					}
				}
				line = br.readLine();
			}
		} catch (IOException ioe) {
            ioe.printStackTrace();
        }

         findLongestFreeTime(week);
    }

    private static void findLongestFreeTime(LinkedHashMap<Date, ArrayList<Event>> map) {
		Date meetingDate = null; 
		LocalTime meetingST = null;
		LocalTime meetingET = null; 
		long longestDifference = 0;

		LocalTime tempST = null;
		LocalTime tempET = null;
		long tempDiff = 0;

    	for(Map.Entry<Date, ArrayList<Event>> entry : map.entrySet()){ 
    		ArrayList<Event> events = entry.getValue();
    		for (int i = 0; i <= events.size(); i++) {
    			if(meetingDate == null ) {
    				meetingDate = entry.getKey(); 
					tempST  = events.get(i).getStartOfDay();
					tempET = events.get(i).getStartTime();
					longestDifference = Duration.between(tempST, tempET).toMinutes();
    			} else if(i == 0) {
    				tempST = events.get(i).getStartOfDay();
    				tempET = events.get(i).getStartTime();
    			} else if (i == events.size()){
    				tempST = events.get(i - 1).getEndTime();
    				tempET = events.get(i - 1).getEndOfDay();
    			} else {
    				if (events.get(i - 1).getEndTime().isBefore(events.get(i).getStartTime())) {
    					tempST = events.get(i - 1).getEndTime();
    					tempET = events.get(i).getStartTime();
    				}
    			} 
	   	
	   			tempDiff = Duration.between(tempST, tempET).toMinutes();
	   			if (tempDiff > longestDifference) {
	   				meetingDate = entry.getKey(); 
					meetingST = tempST;
					meetingET = tempET;
					longestDifference = tempDiff;
	   			}	
			}
    	}
    	System.out.printf("Longest free period from days in the next week where at least one person is busy is: %s from %s to %s", meetingDate.toString(), meetingST.toString(), meetingET.toString());
    }

    private static Date getToday() throws ParseException{
    	Date today = new Date();
		return formatter.parse(formatter.format(today));
    }

    private static Date getWeekFromNow() throws ParseException{
    	Date today = new Date();
		today = formatter.parse(formatter.format(today));

 		Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, 8);
        Date weekFromNow = cal.getTime();

        return formatter.parse(formatter.format(weekFromNow));
    }
}