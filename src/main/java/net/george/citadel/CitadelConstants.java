package net.george.citadel;

import net.george.citadel.config.ServerConfig;

import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("unused")
public class CitadelConstants {
    public static final boolean REMAPREFS = true;
    public static final boolean DEBUG = false;

    private static boolean initDate = false;

    private static boolean aprilFools = false;

    public static boolean isAprilFools(){
        if (!initDate) {
            initDate = true;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            aprilFools = calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1;
        }
        return aprilFools && ServerConfig.aprilFools;
    }
}
