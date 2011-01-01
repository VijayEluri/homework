/*****************
 * Author: MRain
 * Homework 7.4
*****************/
#include <cstdlib>
#include <string>
#include <sstream>
#include <iostream>
using namespace std;

struct IntException {
	string msg;
	const char *errorstr() const { return msg.c_str(); }
	IntException (const char *msg = "Unknown error!") : msg(msg) {}
};

class saveint{
private:
	int data;
	friend ostream &operator <<(ostream &, const saveint &);
	friend istream &operator >>(istream &, const saveint &);
public:
	saveint() {}
	saveint(int a) : data(a) {}
	saveint operator +(const saveint &) const;
	saveint operator -(const saveint &) const;
	saveint operator *(const saveint &) const;
	saveint operator /(const saveint &) const;
	bool operator ==(const saveint &) const;
	bool operator !=(const saveint &) const;
	bool operator <=(const saveint &) const;
	bool operator >=(const saveint &) const;
	bool operator <(const saveint &) const;
	bool operator >(const saveint &) const;
};

saveint saveint::operator +(const saveint &t) const {
	if ((data & (1 << 31)) && (t.data & (1 << 31)))
		throw IntException("Result out of bound.");
	return data + t.data;
}

saveint saveint::operator -(const saveint &t) const {
	if (t.data == 0x80000000)
		throw IntException("Result out of bound.");
	saveint now = ~t.data + 1;
	return *this + now;
}

saveint saveint::operator *(const saveint &t) const {
	long long res = data * t.data;
	if (res > 2147483647ll || res < -2147483648ll)
		throw IntException("Result out of bound.");
	return (int)res;
}

saveint saveint::operator /(const saveint &t) const {
	if (t == 0)
		throw IntException("Divide by zero.");
	return data / t.data;
}

bool saveint::operator ==(const saveint &t) const {
	return data == t.data;
}

bool saveint::operator !=(const saveint &t) const {
	return data != t.data;
}

bool saveint::operator <=(const saveint &t) const {
	return data <= t.data;
}

bool saveint::operator >=(const saveint &t) const {
	return data >= t.data;
}

bool saveint::operator <(const saveint &t) const {
	return data < t.data;
}

bool saveint::operator >(const saveint &t) const {
	return data > t.data;
}

ostream &operator <<(ostream &os, const saveint &t) {
	return os << t.data;
}

istream &operator >>(istream &is, const saveint &t) {
	return is >> t.data;
}
