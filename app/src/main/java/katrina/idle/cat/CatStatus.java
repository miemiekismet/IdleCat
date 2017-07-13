package katrina.idle.cat;

import android.util.Log;
import java.util.Date;

/**
 * Created by katrina on 07/06/2017.
 */
public class CatStatus {
    // Cat
    public Integer hungry = 0;
    public Integer intimacy = 0;
    // Inventory
    public Integer food = 0;
    // System
    public Long last_login_sec = 0L;
    public Date creation_date;
}