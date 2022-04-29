package backend.Player.AI;

public class AIEvent {


    Event event;

    private String description;

    //get/set methods
    public void setEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
