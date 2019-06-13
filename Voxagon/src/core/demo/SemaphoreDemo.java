package core.demo;

import java.util.concurrent.Semaphore;

public class SemaphoreDemo {

	public SemaphoreDemo() {
		Semaphore sem = new Semaphore(1);
		Thread th = new Thread() {
			public void run() {
				try {
					sem.acquire();
					System.out.println("Thread B got the semaphore");
					Thread.sleep(5000);
					System.out.println("Thread B is going to release the semaphore");
					sem.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		};
		th.start();
		try {
			Thread.sleep(400);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			System.out.println("Thread A wants the semaphore");
			sem.acquire();
			System.out.println("Thread A got the semaphore");
			sem.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		new SemaphoreDemo();
	}

}
