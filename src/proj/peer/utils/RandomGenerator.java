package proj.peer.utils;

import java.util.Random;

public class RandomGenerator {

    public static int getNumberInRange(int min, int max) {

        if (min >= max) {
            int tmp = min;
            min = max;
            max = tmp;
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

}
