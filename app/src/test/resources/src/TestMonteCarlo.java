import java.util.Random;

public class TestMonteCarlo {

    public static double monte() {
        Random r = new Random(System.currentTimeMillis());
        int cnt = 0;
        final int n = 4000000;
        double x,y;
        for (int i = 0; i < n; i++) {
            x = r.nextDouble();
            y = r.nextDouble();
            if(x * x + y * y <= 1){
               cnt++; 
            }
        }
        return (double)cnt / (double)n * 4D;
    }

    public static double monte2() {
        Random r = new Random(System.currentTimeMillis());
        int cnt = 0;
        final int n = 4000000;
        double x,y;
        for (int i = 0; i < n; i++) {
            x = r.nextDouble();
            y = r.nextDouble();
            if(x * x + y * y <= 1){
               cnt++; 
            }
        }        
        return (double)cnt / (double)n * 4D;
    }

    public static void main(String[] args) {
        double m = monte();
        double m2 = monte2();
        System.out.println(m + " " + m2);
    }
}
