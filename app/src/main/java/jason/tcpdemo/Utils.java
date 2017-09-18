package jason.tcpdemo;

/**
 * Created by djf on 2017/9/18.
 */

public class Utils {

    public static String getThreadInfo(Thread thread) {
        if (thread != null) {
            return "Name:" + thread.getName()
                    + "    ThreadGroup:" + thread.getThreadGroup().getName()
                    + "    Priority:" + thread.getPriority()
                    + "    Id:" + thread.getId()
                    + "    State:" + thread.getState();
        }
        return null;
    }
}
