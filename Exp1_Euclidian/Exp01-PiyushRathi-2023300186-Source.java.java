package Exp1_Euclidian;

import java.util.Scanner;

public class menu {

    // A) Basic Euclidean Algorithm (Tabular Method)
    static void basicEuclidean(int a, int b) {
        int r1 = a, r2 = b;
        System.out.println("q\tr1\tr2\tr");
        while (r2 != 0) {
            int q = r1 / r2;
            int r = r1 % r2;
            System.out.println(q + "\t" + r1 + "\t" + r2 + "\t" + r);
            r1 = r2;
            r2 = r;

            System.out.println("GCD = " + r1);
        }
    }

    // B) Extended Euclidean Algorithm (Tabular Method)
    static void extendedEuclidean(int a, int b) {
        int r1 = a, r2 = b;
        int s1 = 1, s2 = 0;
        int t1 = 0, t2 = 1;

        System.out.println("q\tr1\tr2\tr\ts1\ts2\ts\tt1\tt2\tt");
        while (r2 != 0) {
            int q = r1 / r2;
            int r = r1 % r2;
            int s = s1 - s2 * q;
            int t = t1 - t2 * q;

            System.out.println(q + "\t" + r1 + "\t" + r2 + "\t" + r +
                    "\t" + s1 + "\t" + s2 + "\t" + s +
                    "\t" + t1 + "\t" + t2 + "\t" + t);

            // Shift
            r1 = r2;
            r2 = r;
            s1 = s2;
            s2 = s;
            t1 = t2;
            t2 = t;
        }
        System.out.println("GCD = " + r1 + ", s = " + s1 + ", t = " + t1);
    }

    // C) Euclidean Algorithm for GCD and Multiplicative Inverse (MI)
    static void multiplicativeInverse(int a, int b) {
        int r1 = a, r2 = b;
        int t1 = 0, t2 = 1;

        System.out.println("q\tr1\tr2\tr\tt1\tt2\tt");
        while (r2 != 0) {
            int q = r1 / r2;
            int r = r1 % r2;
            int t = t1 - t2 * q;

            System.out.println(q + "\t" + r1 + "\t" + r2 + "\t" + r +
                    "\t" + t1 + "\t" + t2 + "\t" + t);

            // Shift
            r1 = r2;
            r2 = r;
            t1 = t2;
            t2 = t;
        }
        System.out.println("GCD = " + r1);
        if (r1 == 1) {
            int mi = (t1 % b + b) % b; // ensure positive
            System.out.println("Multiplicative Inverse of " + a + " mod " + b + " = " + mi);
        } else {
            System.out.println("No Multiplicative Inverse exists (since GCD ≠ 1)");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Basic Euclidean Algorithm (GCD)");
            System.out.println("2. Extended Euclidean Algorithm (GCD, s, t)");
            System.out.println("3. Multiplicative Inverse");
            System.out.println("4. Quit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            if (choice == 4) {
                System.out.println("Exiting program...");
                break;
            }

            System.out.print("Enter first number (a): ");
            int a = sc.nextInt();
            System.out.print("Enter second number (b): ");
            int b = sc.nextInt();

            switch (choice) {
                case 1:
                    basicEuclidean(a, b);
                    break;
                case 2:
                    extendedEuclidean(a, b);
                    break;
                case 3:
                    multiplicativeInverse(a, b);
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
        sc.close();
    }
}