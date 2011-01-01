/*****************
 * Author: MRain
 * Homework 7.1
*****************/
#include <iostream>
#include <cstdlib>
#include <algorithm>
#include <vector>
using namespace std;

template <class T> class set{
public:
	vector <T> data;
	set() { data.clean(); }
	bool has(const T& t) const {
		for (int i = 0; i < (int)data.size(); ++ i)
			if (data[i] == t) return true;
		return false;
	}
	void operator |=(const set& t) {
		for (int i = 0; i < (int)t.data.size(); ++ i)
			if (!has(t.data[i])) data.push_back(t.data[i]);
	}
	void operator ^=(const set& t) {
		vector <T> newdata;
		for (int i = 0; i < (int)t.data.size(); ++ i)
			if (!has(t.data[i])) newdata.push_back(t.data[i]);
		data = newdata;
	}
	void operator &=(const set& t) {
		vector <T> newdata;
		for (int i = 0; i < (int)t.data.size(); ++ i)
			if (has(t.data[i])) newdata.push_back(t.data[i]);
		data = newdata;
	}
	set operator |(const set& t) {	//并
		set tmp = t;
		tmp |= *this;
		*this = tmp;
	}
	set operator ^(const set& t) {	//差
		set tmp = t;
		tmp ^= *this;
		*this = tmp;
	}
	set operator &(const set& t) {	//交
		set tmp = t;
		tmp &= *this;
		*this = tmp;
	}
};

