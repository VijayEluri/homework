#include "TreeSet.h"
#include <iostream>
#include <ctime>
#include <cstdlib>
#include <vector>
#include <set>
using namespace std;

int main() {
	set <int> s;
	vector <int> p;
	srand(time(0));
	for (int i = 0; i < 200000; ++ i) {
		int k = rand();
		s.insert(k);
		p.push_back(k);
	}
	for (int i = 0; i < 100000; ++ i)
		if (s.count(p[i]))
			s.erase(p[i]);
	cout << s.size() << endl;
	s.clear();
	return 0;
	/*
	TreeSet <int> s;
	vector <int> p;
	srand(time(0));
	for (int i = 0; i < 200000; ++ i) {
		int k = rand();
		s.add(k);
		p.push_back(k);
	}
	for (int i = 0; i < 100000; ++ i)
		if (s.contains(p[i]))
			s.remove(p[i]);
	cout << s.size() << endl;
	cout << s.last() << endl;
	cout << s.first() << endl;
	s.clear();*/
	/*
	TreeSet<int>::ConstIterator it = s.constIterator();
	while (it.hasNext())
		cout << it.next() << endl;*/
	return 0;
}

