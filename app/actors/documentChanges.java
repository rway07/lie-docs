package actors;

/**
 * Created by enrico on 05/10/16.
 */
public class documentChanges extends Object {
    private final Object msg;
    public documentChanges(Object msg){this.msg = msg;}
    public Object getMsg(){return msg;};
}
