package messages;

import akka.actor.ActorRef;

/**
 * Created by enrico on 18/10/16.
 */
public class updateCompile {
    private String status;
    private ActorRef sender;

    public String getSenderName() {
        return senderName;
    }

    public updateCompile setSenderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    private String senderName;

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
