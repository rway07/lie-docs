package messages;

/**
 * Created by enrico on 25/10/16.
 */
public class deleteProject {
    private int id;
    private String project;

    public deleteProject(int id,String project){
        this.id = id; this.project = project;
    }

    public int getId() {
        return id;
    }

    public String getProject() {
        return project;
    }
}
