public class NumberSequence implements Runnable {

    public static void main(String[] args) {
        NumberSequence main1 = new NumberSequence();
        NumberSequence main2 = new NumberSequence();
        Thread thread1 = new Thread(main1);
        Thread thread2 = new Thread(main2);
        thread1.setName("th1 = ");
        thread2.setName("th2 = ");
        thread1.start();
        thread2.start();
    }

    public void run() {
        boolean isUpDirection = true;
        int cnt = 20;
        int count = 1;

        while (--cnt>0) {
            Print.print(Thread.currentThread().getName(), String.valueOf(count));
            count = isUpDirection ? count+1 : count-1;
            if (count ==10)
                isUpDirection= false;
            if (count == 0 )
                isUpDirection = true;
        }
        Print.close();
    }
}