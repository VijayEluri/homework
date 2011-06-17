#include <cstdio>
#include <ctime>
#include <cstdlib>
#include <iostream>
#include <algorithm>
#include "HashSet.h"
using namespace std;

class TT {
	public:
		static int hashcode(int a) {
			return a;
		}
};

int main() {
	srand(time(0));
	HashSet<int, TT> h;
	HashSet<int, TT>::Iterator it = h.iterator();
	for (int i = 0; i < 100; ++ i)
		h.add(i);
	while (it.hasNext())
		cout << it.next()<< endl;
	cout << h.size() << endl;
	h.clear();
	cout << h.size() << endl;
	return 0;
}

