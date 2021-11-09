public class Test1 {
  public static int fact(int n) {
    if(n == 0)
      return 1;
    int res = n * fact(n-1);
    return res;
  }

  public static int fact2(int n) {
    if(n == 0)
      return 1;
    int res = n * fact(n-1);
    return res;
  }

  public static void main(String[] args) {
    System.out.println("Coucou: " + fact(4));
    System.out.println("Coucou: " + fact2(4));
  }
}
