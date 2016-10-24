package messages;

import scala.Serializable;

/**
 * Created by enrico on 21/10/16.
 */
public class controllerMessage implements Serializable {

    public enum actionEnum{DELETE,ADD};
    public enum targetEnum{FILE,PROJECT};

    private actionEnum action;
    private targetEnum target;
    private int targetID;
    private int containerID;
    private String targetName;
    private boolean requireVote=false;
    private String containerName;
    private String author;
    private boolean forwarded = false;
    private boolean ack = false;

    public boolean isAck() {
        return ack;
    }

    public controllerMessage setAck(boolean ack) {
        this.ack = ack;
        return this;
    }

    public referendumMessage toReferedumMessage(){

        referendumMessage msg = new referendumMessage();
        msg.setTargetID(targetID);
        msg.setContainerID(containerID);
        msg.setTargetName(targetName);
        msg.setRequireVote(requireVote);
        msg.setContainerName(containerName);
        msg.setAuthor(author);
        msg.setForwarded(forwarded);
        msg.setTarget(target);
        msg.setAction(action);

        return msg;
    }

    public boolean isForwarded() {
        return forwarded;
    }

    public controllerMessage setForwarded(boolean forwarded) {
        this.forwarded = forwarded;
        return this;
    }

    public int getQuorum() {
        return quorum;
    }

    public controllerMessage setQuorum(int quorum) {
        this.quorum = quorum;
        return this;
    }

    private int quorum;

    public String getAuthor() {
        return author;
    }

    public controllerMessage setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getContainerName() {
        return containerName;
    }

    public controllerMessage setContainerName(String containerName) {
        this.containerName = containerName;
        return this;
    }

    public boolean isRequireVote() {
        return requireVote;
    }

    public controllerMessage setRequireVote(boolean requireVote) {
        this.requireVote = requireVote;
        return this;
    }

    public String getTargetName() {
        return targetName;
    }

    public controllerMessage setTargetName(String targetName) {
        this.targetName = targetName;
        return this;
    }

    public actionEnum getAction() {
        return action;
    }

    public controllerMessage setAction(actionEnum action) {
        this.action = action;
        return this;
    }

    public targetEnum getTarget() {
        return target;
    }

    public controllerMessage setTarget(targetEnum target) {
        this.target = target;
        return this;
    }

    public int getTargetID() {
        return targetID;
    }

    public controllerMessage setTargetID(int targetID) {
        this.targetID = targetID;
        return this;
    }

    public int getContainerID() {
        return containerID;
    }

    public controllerMessage setContainerID(int containerID) {
        this.containerID = containerID;
        return this;
    }




}
