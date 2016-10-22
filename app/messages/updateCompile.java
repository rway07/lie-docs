package messages;

import akka.actor.ActorRef;
import scala.Serializable;
import utils.jsonUtil;

/**
 * Created by enrico on 18/10/16.
 */
public class updateCompile implements Serializable {
    private String status;
    private ActorRef sender;
    private int totalSteps;
    private int currentStep;
    private type msgType;
    public enum type{Error,Info,Progress,Success};
    private String senderName;

    public String toString(){
        jsonUtil ret = new jsonUtil("");
        ret.put("fn","console");
        ret.put("status",status);
        ret.put("totalSteps",totalSteps);
        ret.put("currentStep",currentStep);
        ret.put("msgType",msgType.toString());
        ret.put("sender",senderName);

        return ret.toString();
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public updateCompile setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
        return this;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public updateCompile setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
        return this;
    }

    public type getMsgType() {
        return msgType;
    }

    public updateCompile setMsgType(type msgType) {
        this.msgType = msgType;
        return this;
    }

    public String getSenderName() {
        return senderName;
    }

    public updateCompile setSenderName(String senderName) {
        this.senderName = senderName;
        return this;
    }



    public ActorRef getSender() {
        return sender;
    }

    public updateCompile setSender(ActorRef sender) {
        this.sender = sender;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public updateCompile setStatus(String status) {
        this.status = status;
        return this;
    }
}
