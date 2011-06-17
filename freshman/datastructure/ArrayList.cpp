#include "ArrayList.h"
#include <iostream>
using namespace std;

int main() {
	typedef ArrayList<int> t0;
	typedef ArrayList<t0> t1;
	int p = 0;
	t0 s0;
	t1 s1;
	for (int i = 0; i < 90; ++ i) {
		s0.add(++ p);
		s1.add(s0);
	}
	cout << s1.get(9).get(8) << endl;
	return 0;
}
