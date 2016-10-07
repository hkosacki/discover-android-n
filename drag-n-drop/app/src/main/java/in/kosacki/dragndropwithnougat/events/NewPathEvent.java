package in.kosacki.dragndropwithnougat.events;

/**
 * Created by hubert on 27/09/16.
 */

public class NewPathEvent {

    private String path;

    public NewPathEvent(String pPath){
        path = pPath;
    }

    public String getPath(){
        return path;
    }

}
