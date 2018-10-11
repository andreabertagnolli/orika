package ma.glasnost.orika.test.community.issue121.util;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author: Ilya Krokhmalyov YC14IK1
 * @since: 8/23/13
 */

public class RandomUtils {
    private static Random random = new Random();

    public static int randomInt() {
        return random.nextInt();
    }

    public static String randomString() {
        return RandomStringUtils.randomAlphabetic(10);
    }
}
