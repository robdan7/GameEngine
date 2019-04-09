package core.demo;

import core.utils.datatypes.GlueList;

public class ListDemo {

	public ListDemo() {
		GlueList<Integer> list = new GlueList<Integer>();
		
		add(list,3);
		add(list,1);
		add(list,2);
		add(list,5);
		add(list,0);
		add(list,4);
		System.out.println(list);
	}
	
	private void add(GlueList<Integer> list, int index) {
		int i = 0;
		for (; i < list.size() && i < index; i++) {
			if (list.get(i) > index) {
				break;
			}
		}
		list.add(i,index);
	}

	public static void main(String[] args) {
		new ListDemo();
	}
}
