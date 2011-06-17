#include <cstdio>
#include <ctime>
#include <cstdlib>
#include <iostream>
#include <algorithm>
#include "HashMap.h"
using namespace std;

class TT {
	public:
		static int hashcode(int a) {
			return a;
		}
};

int main() {
	HashMap <int, int, TT> s;
	s.put(1, 1);
	s.put(1, 2);
	cout << s.get(1) << endl;
	return 0;
}

