public class TestSumLoop {

  private static int[] arr = {1, 2, 3, 4, 5, 6};

  public static double compute() {
    double sum = 0;
    for (int i = 0; i < arr.length; i++) {
      sum += arr[i];
    }
    return sum;
  }

  public static int compute2() {
    int sum = 0;
    for (int i = 0; i < arr.length; i++) {
      sum += arr[i];
    }
    return sum;
  }

  public static void main(String[] args) {
    double res = compute();
    int res2 = compute2();
    System.out.println(res + " " + res2);
  }
}
