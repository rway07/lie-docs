package messages;

import akka.actor.ActorRef;

/**
 * Created by enrico on 18/10/16.
 */
public class sourceCompiled {

    private byte[] binData = null;
    private String objName = "";
    private boolean compilationFailed = false;
    private ActorRef sender;

    public String getWorkerName() {
        return workerName;
    }

    public sourceCompiled setWorkerName(String workerName) {
        this.workerName = workerName;
        return this;
    }

    private String workerName = "";

    public ActorRef getSender() {
        return sender;
    }

    public sourceCompiled setSender(ActorRef sender) {
        this.sender = sender;
        return this;
    }

    public String getObjName() {
        return objName;
    }

    public boolean isCompilationFailed() {
        return compilationFailed;
    }

    public byte[] getBinData() {
        return binData;
    }

    public sourceCompiled setCompilationFailed(boolean compilationFailed) {
        this.compilationFailed = compilationFailed;
        return this;
    }

    public sourceCompiled setObjName(String objName) {
        this.objName = objName;
        return this;
    }

    public sourceCompiled setBinData(byte[] binData) {
        this.binData = binData;
        return this;
    }
}
