import java.time.LocalTime;

public class Event {
	private final LocalTime START_OF_DAY = LocalTime.parse("08:00:00");
	private final LocalTime END_OF_DAY = LocalTime.parse("22:00:00");
	private LocalTime startTime;
	private LocalTime endTime;

	public Event(LocalTime startTime, LocalTime endTime) {
		this.startTime = startTime.isBefore(START_OF_DAY) ? START_OF_DAY : startTime; 
		this.endTime = endTime.isAfter(END_OF_DAY) ? END_OF_DAY : endTime; 
	}

	public void update (LocalTime newStartTime, LocalTime newEndTime) {
		if (newStartTime.isBefore(this.startTime)) {
			this.startTime = newStartTime.isBefore(START_OF_DAY) ? START_OF_DAY : newStartTime; 
		}
		if (newEndTime.isAfter(this.endTime)) {
			this.endTime = newEndTime.isAfter(END_OF_DAY) ? END_OF_DAY : newEndTime; 
		}
	}

	public boolean contains(LocalTime time) {
		if (time.isAfter(startTime) && time.isBefore(endTime)) {
			return true;
		} 
		return false;
	}

	public boolean isBetween(LocalTime st, LocalTime et) {
		if (startTime.isAfter(st) && endTime.isBefore(et)) {
			return true;
		} 
		return false;
	}

	public String time() {
		return this.startTime.toString() + " - " + this.endTime.toString();
	}

	public LocalTime getStartTime() {
		return this.startTime;
	}

	public LocalTime getEndTime() {
		return this.endTime;
	}

	public LocalTime getStartOfDay() {
		return START_OF_DAY;
	}

	public LocalTime getEndOfDay() {
		return END_OF_DAY;
	}
}

