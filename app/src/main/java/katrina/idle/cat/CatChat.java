package katrina.idle.cat;

import android.content.res.Resources;

/**
 * Created by katrina on 07/06/2017.
 */
public class CatChat {
    public CatChat() {}
    public Integer chat(Integer size){
        Double r = Math.floor(Math.random() * size);
        return r.intValue();
    }
}
