#include <cstdio>
#include <iomanip>
#include <cassert>
#include <cstdlib>
#include <ctime>
#include <cmath>
#include "ArrayList.h"
#include "LinkedList.h"
#include "TreeSet.h"
#include "TreeMap.h"
#include "HashSet.h"
#include "HashMap.h"
#include <algorithm>
#include <iostream>
#include <set>
#include <map>
#include <vector>
using namespace std;
#define RUN(test) {\
		cout << #test << ':' << endl; \
		clock_t start, finish; \
		start = clock(); \
		bool res = test(); \
		finish = clock(); \
		double duration = (double)(finish - start) / CLOCKS_PER_SEC; \
		cout << "\tDuration Time: " << duration << 's' << endl;\
		cout << "\tResult: " << (res ? "Pass" : "Failed") << endl; \
		cout << endl; \
	}

class Hash {
public:
	static int hashcode(int a) {
		return a;
	}
};

bool ArrayList_Test() {
	ArrayList<int> t;
	for (int i = 0; i < 10000; ++ i)
		t.add(i);
	for (int i = 0; i < 10000; ++ i)
		if (t.get(i) != i) return false;
	t.removeRange(0, 9999);
	if (t.size() != 1) return false;
	t.clear();

	for (int i = 0; i < 10000; ++ i)
		t.add(i);
	for (int i = 0; i < 5000; ++ i)
		t.removeIndex(0);
	for (int i = 0; i < 5000; ++ i)
		if (t.get(i) - 5000 != i) return false;
	for (int i = 0; i < 500; ++ i)
		if (t.remove(i)) return false;
	t.removeRange(0, 1000);
	if (t.size() != 4000) return false;

	t = t.subList(1000, 2000);
	if (t.indexOf(7101) != 101) return false;

	int tmpcnt = 7000;
	ArrayList<int>::ConstIterator cit = t.constIterator();
	while (cit.hasNext())
		if (cit.next() != tmpcnt ++) return false;
	if (tmpcnt != 8000) return false;

	tmpcnt = 7000;
	ArrayList<int>::Iterator it = t.iterator();
	while (it.hasNext())
		if (it.next() != tmpcnt ++) return false;
	if (tmpcnt != 8000) return false;

	it = t.iterator();
	for (int i = 0; i < 100; ++ i)
		it.next();
	it.remove();
	if (t.get(100) != 7101) return false;

	t.add(99, 7099);
	for (int i = 7000; i < 8000; ++ i)
		if (t.get(i - 7000) != i)
			return false;

	for (int i = 0; i < t.size(); ++ i)
		t.set(i, 1);
	if (t.contains(2)) return false;

	return true;
}

bool TreeSet_Insert_Test() {
	TreeSet<int> t;
	for (int i = 0; i < 500000; ++ i)
		t.add(rand());
	for (int i = 0; i < 500000; ++ i)
		t.contains(rand());
	TreeSet<int>::Iterator it = t.iterator();
	while (it.hasNext())
		it.next();
	return true;
}

bool HashSet_Insert_Test() {
	HashSet<int, Hash> t;
	for (int i = 0; i < 500000; ++ i)
		t.add(rand());
	for (int i = 0; i < 500000; ++ i)
		t.contains(rand());
	HashSet<int, Hash>::Iterator it = t.iterator();
	while (it.hasNext())
		it.next();
	return true;
}

bool set_Insert_Test() {
	set<int> t;
	for (int i = 0; i < 500000; ++ i)
		t.insert(rand());
	for (int i = 0; i < 500000; ++ i)
		t.count(rand());
	set<int>::iterator it = t.begin();
	while (it != t.end()) ++ it;
	return true;
}

bool TreeSet_Test() {
	for (int tests = 0; tests < 10; ++ tests) {
		TreeSet<int> t;
		set<int> cor;
		set<int>::iterator cit;
		for (int i = 0; i < 1000; ++ i) {
			int k = rand();
			t.add(k);
			cor.insert(k);
		}

		TreeSet<int>::Iterator it = t.iterator();
		cit = cor.begin();
		while (cit != cor.end()) {
			if (!it.hasNext()) return false;
			if (it.next() != *cit) return false;
			++ cit;
		}
		if (it.hasNext()) return false;

		for (int i = 0; i < 10000; ++ i) {
			int p = rand();
			if (cor.count(p) && !t.contains(p)) return false;
			if (!cor.count(p) && t.contains(p)) return false;
		}

		for (cit = cor.begin(); cit != cor.end(); ++ cit)
			if (!t.contains(*cit)) return false;

		cit = cor.begin(); it = t.iterator();
		it.next();
		it.remove(); cor.erase(cit);
		it = t.iterator();
		cit = cor.begin();
		while (cit != cor.end()) {
			if (!it.hasNext()) return false;
			if (it.next() != *cit) return false;
			++ cit;
		}
		if (it.hasNext()) return false;

		cit = cor.begin(); t.remove(*cit); cor.erase(cit);
		it = t.iterator();
		cit = cor.begin();
		while (cit != cor.end()) {
			if (!it.hasNext()) return false;
			if (it.next() != *cit) return false;
			++ cit;
		}
		if (it.hasNext()) return false;

		t.clear();
		if (!t.isEmpty()) return false;
		if (t.size() != 0) return false;
	}
	
	return true;
}

bool HashSet_Test() {
	for (int tests = 0; tests < 10; ++ tests) {
		HashSet<int, Hash> t;
		set<int> cor;
		set<int>::iterator cit;
		for (int i = 0; i < 1000; ++ i) {
			int k = rand();
			t.add(k);
			cor.insert(k);
		}

		HashSet<int, Hash>::Iterator it = t.iterator();
		while (it.hasNext())
			if (!cor.count(it.next())) return false;

		for (int i = 0; i < 10000; ++ i) {
			int p = rand();
			if (cor.count(p) && !t.contains(p)) return false;
			if (!cor.count(p) && t.contains(p)) return false;
		}

		for (cit = cor.begin(); cit != cor.end(); ++ cit)
			if (!t.contains(*cit)) return false;

		it = t.iterator();
		cor.erase(it.next());
		it.remove();
		it = t.iterator();
		while (it.hasNext())
			if (!cor.count(it.next())) return false;

		cit = cor.begin(); t.remove(*cit); cor.erase(cit);
		it = t.iterator();
		while (it.hasNext())
			if (!cor.count(it.next())) return false;

		t.clear();
		if (!t.isEmpty()) return false;
		if (t.size() != 0) return false;
	}
	
	return true;
}

bool ArrayList_Insert_Test() {
	ArrayList<int> t;
	for (int i = 0; i < 10000000; ++ i)
		t.add(i);
	ArrayList<int>::ConstIterator it = t.constIterator();
	while (it.hasNext()) it.next();
	return true;
}

bool LinkedList_Test() {
	LinkedList<int> t;
	for (int i = 0; i < 1000; ++ i)
		t.add(i);
	if (t.getFirst() != 0) return false;
	if (t.getLast() != 999) return false;
	for (int i = 0; i < 1000; ++ i)
		if (t.removeFirst() != i) return false;
	if (t.size()) return false;

	for (int i = 0; i < 1000; ++ i)
		t.addFirst(i);
	for (int i = 999; i >= 0; -- i)
		if (t.removeLast() != 999 - i)
			return false;
	if (t.size()) return false;

	for (int i = 0; i < 10000; ++ i)
		t.add(i);
	for (int i = 0; i < 5000; ++ i)
		t.removeIndex(0);
	for (int i = 0; i < 5000; ++ i)
		if (t.get(i) - 5000 != i) return false;
	for (int i = 0; i < 500; ++ i)
		if (t.remove(i)) return false;
	if (t.size() != 5000) return false;

	t = t.subList(1000, 2000);
	if (t.indexOf(6101) != 101) return false;

	int tmpcnt = 6000;
	LinkedList<int>::ConstIterator cit = t.constIterator();
	while (cit.hasNext())
		if (cit.next() != tmpcnt ++) return false;
	if (tmpcnt != 7000) return false;

	tmpcnt = 6000;
	LinkedList<int>::Iterator it = t.iterator();
	while (it.hasNext())
		if (it.next() != tmpcnt ++) return false;
	if (tmpcnt != 7000) return false;

	it = t.iterator();
	for (int i = 0; i < 100; ++ i)
		it.next();
	it.remove();
	if (t.get(100) != 6101) return false;

	t.add(99, 6099);
	for (int i = 6000; i < 7000; ++ i)
		if (t.get(i - 6000) != i)
			return false;

	for (int i = 0; i < t.size(); ++ i)
		t.set(i, 1);
	if (t.contains(2)) return false;

	return true;
}

bool TreeMap_Test() {
	TreeMap<int, double> t;
	for (int i = 0; i < 13; ++ i)
		t.put(i, i);

	TreeMap<int, double>::Iterator it = t.iterator();
	while (it.hasNext()) {
		Entry<int, double> tmp = it.next();
		if (tmp.key != tmp.value) return false;
	}
	TreeMap<int, double>::ConstIterator cit = t.constIterator();
	while (cit.hasNext()) {
		Entry<int, double> tmp = cit.next();
		if (tmp.key != tmp.value) return false;
	}
	for (int i = 0; i < 13; ++ i)
		if (t.get(i) != i) return false;

	t.put(10, 0);
	if (t.size() != 13) return false;
	if (t.firstKey() != 0) return false;
	if (t.lastKey() != 12) return false;
	if (t.lastEntry().value != 12 || t.lastEntry().key != 12) return false;
	if (t.firstEntry().value != 0 || t.firstEntry().key != 0) return false;
	if (t.containsValue(10)) return false;
	if (!t.containsKey(10)) return false;
	t.remove(10);
	t.remove(0);
	if (t.containsValue(0)) return false;
	if (t.size() != 11) return false;
	if (t.containsKey(0)) return false;

	t.clear();
	return true;
}

bool HashMap_Test() {
	HashMap<int, double, Hash> t;
	for (int i = 0; i < 13; ++ i)
		t.put(i, i);

	HashMap<int, double, Hash>::Iterator it = t.iterator();
	while (it.hasNext()) {
		Entry<int, double> tmp = it.next();
		if (tmp.key != tmp.value) return false;
	}
	HashMap<int, double, Hash>::ConstIterator cit = t.constIterator();
	while (cit.hasNext()) {
		Entry<int, double> tmp = cit.next();
		if (tmp.key != tmp.value) return false;
	}
	for (int i = 0; i < 13; ++ i)
		if (t.get(i) != i) return false;

	t.put(10, 0);
	if (t.size() != 13) return false;
	if (t.containsValue(10)) return false;
	if (!t.containsKey(10)) return false;
	t.remove(10);
	t.remove(0);
	if (t.containsValue(0)) return false;
	if (t.size() != 11) return false;
	if (t.containsKey(0)) return false;

	t.clear();
	return true;
}

void Comparation_Set() {
	cout << "Comparation between TreeSet and std::set" << endl;
	TreeSet<int> t; 
}

int main() {
	cout.setf(ios::fixed);
	cout.precision(3);
	srand(time(0));
	RUN(ArrayList_Test);
	RUN(TreeSet_Test);
	RUN(HashSet_Test);
	RUN(LinkedList_Test);
	RUN(TreeMap_Test);
	RUN(HashMap_Test);
	RUN(ArrayList_Insert_Test);
	RUN(TreeSet_Insert_Test);
	RUN(HashSet_Insert_Test);
	RUN(set_Insert_Test);

	//Special Part
	Comparation_Set();
	return 0;
}
