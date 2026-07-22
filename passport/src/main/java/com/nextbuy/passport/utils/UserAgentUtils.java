package com.nextbuy.passport.utils;

import ua_parser.Client;
import ua_parser.Parser;

public class UserAgentUtils {

    private UserAgentUtils() {}

    public static String getDeviceInfo(String userAgent) {
        Parser uaParser = new Parser();
        Client c = uaParser.parse(userAgent);

        String deviceFamily = c.device.family != null ? c.device.family : "UnknownDevice";
        String osFamily = c.os.family != null ? c.os.family : "UnknownOS";
        String osMajor = c.os.major != null ? c.os.major : "0";
        String osMinor = c.os.minor != null ? c.os.minor : "0";

        return String.format("Device:%s;OS:%s %s.%s",
                deviceFamily,
                osFamily,
                osMajor,
                osMinor
        );

    }

}
