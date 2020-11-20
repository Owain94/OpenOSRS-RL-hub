package renderer;

public class BinarySearch {
    public static BinarySearch instance = null;
    private int min = 0;
    private int max = 0xffff;

    public void yes() {
        min = (int) Math.ceil((min + max) / 2.);
    }

    public void no() {
        max = (int) Math.floor((min + max) / 2.);
    }

    public boolean test(int n) {
        return n >= (min + max) / 2. && n <= max;
    }

    public String toString() {
        if (min == max) {
            return "" + min;
        } else {
            return "between " + min + " and " + max;
        }
    }
}
