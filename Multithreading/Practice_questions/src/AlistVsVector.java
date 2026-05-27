//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() throws InterruptedException {
    ArrayList<Integer> list = new ArrayList<>();
    Vector<Integer> vector = new Vector<>();

    long startTime = System.currentTimeMillis();
    Thread t1 = new Thread(new UnsafeArrayList(list));
    Thread t2 = new Thread(new UnsafeArrayList(list));
    t1.start();t2.start();
    t1.join();t2.join();
    long endTime = System.currentTimeMillis();
    System.out.println("End thread implementation duration: "+(endTime-startTime));
    System.out.println("List size "+list.size());
    startTime = System.currentTimeMillis();
    Thread t3 = new Thread(new SafeVector(vector));
    Thread t4 = new Thread(new SafeVector(vector));
    t3.start();t4.start();
    t3.join();t4.join();
    endTime = System.currentTimeMillis();
    System.out.println("End vector implementation duration: "+(endTime-startTime));
    System.out.println("Vector size "+vector.size());
}

public class UnsafeArrayList implements Runnable{
    private ArrayList<Integer> list;
    public UnsafeArrayList(ArrayList<Integer> list){
        this.list = list;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1_000_000; i++) list.add(i);
    }
}

public class SafeVector implements Runnable{
    private Vector<Integer> list;
    public SafeVector(Vector<Integer> list){
        this.list = list;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1_000_000; i++) list.add(i);
    }
}
