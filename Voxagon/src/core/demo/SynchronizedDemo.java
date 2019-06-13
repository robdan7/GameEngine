package core.demo;

public class SynchronizedDemo {

	public SynchronizedDemo() {
		Thread th = new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("hello from thread B");
			}
		};
		
		th.start();
		//th.run();
		System.out.println("hello world from thread A");
	}

	public static void main(String[] args) {
		new SynchronizedDemo();
		/*Test t = new Test();
		
		Thread th = new Thread(){
			public void run() {
				t.increase();
			}
		};
		
		th.run();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.decrease();
		*/
	}

}

class Test {
	private int t = 0;
	
	synchronized void increase() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.t++;
		System.out.println("t is increased");
	}
	
	synchronized void decrease() {
		this.t--;
		System.out.println("t is decreased");
	}
}
