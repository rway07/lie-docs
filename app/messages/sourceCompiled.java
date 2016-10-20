package messages;

/**
 * Created by enrico on 18/10/16.
 */
public class sourceCompiled {

    byte[] binData = null;
    String objName = "";

    public sourceCompiled setObjName(String objName) {
        this.objName = objName;
        return this;
    }

    public sourceCompiled setBinData(byte[] binData) {
        this.binData = binData;
        return this;
    }
}
