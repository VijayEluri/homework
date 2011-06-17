#include "LinkedList.h"
#include <iostream>
#include <cassert>
using namespace std;

int main() {
	LinkedList<int> t;
	for (int i = 0; i < 10000; ++ i)
		t.add(i);
	cout << t.removeIndex(9) << endl;
	assert(t.size() == 9999);
	cout << t.removeFirst() << endl;
	cout << t.removeFirst() << endl;
	cout << t.removeLast() << endl;
	cout << t.size() << endl;
	t = t.subList(1, 20);
	cout << t.get(10) << endl;
	cout << t.contains(10) << endl;
	return 0;
}

