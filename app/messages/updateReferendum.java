package messages;

/**
 * Created by enrico on 22/10/16.
 */
public class updateReferendum extends referendumMessage {

    public enum enumObject {VOTER,RANK};
    private enumObject object;
    private Object value;

    public enumObject getObject() {
        return object;
    }

    public updateReferendum setObject(enumObject object) {
        this.object = object;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public updateReferendum setValue(Object value) {
        this.value = value;
        return this;
    }





}
