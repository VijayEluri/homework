/*****************
 * Author: MRain
 * Homework 5.1
*****************/
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <algorithm>
#include <iostream>
#include <sstream>
using namespace std;

class LongLongInt {
public:
	char data[128];
	int len;
	LongLongInt();
	LongLongInt(const char *);
	LongLongInt(int);
	bool operator ==(const LongLongInt &t) const;
	bool operator !=(const LongLongInt &t) const;
	bool operator <(const LongLongInt &t) const;
	bool operator <=(const LongLongInt &t) const;
	bool operator >(const LongLongInt &t) const;
	bool operator >=(const LongLongInt &t) const;
	LongLongInt operator +(const LongLongInt &t) const;
	char *toString() const;
};

LongLongInt::LongLongInt() {
	memset(data, 0, sizeof(data));
	len = 1;
}

LongLongInt::LongLongInt(const char *p) {
	memset(data, 0, sizeof(data));
	len = min((int)strlen(p), 128);
	for (int i = 0; i < len; ++ i)
		data[len - i - 1] = p[i] - '0';
}

LongLongInt::LongLongInt(int p) {
	memset(data, 0, sizeof(data));
	len = 0;
	while (p) {
		data[len ++] = p % 10;
		p /= 10;
	}
	if (!len) len = 1;
}

bool LongLongInt::operator ==(const LongLongInt &t) const {
	if (len != t.len) return false;
	for (int i = 0; i < len; ++ i)
		if (data[i] != t.data[i]) return false;
	return true;
}

bool LongLongInt::operator !=(const LongLongInt &t) const {
	return !(*this == t);
}

bool LongLongInt::operator <(const LongLongInt &t) const {
	if (len != t.len) return len < t.len;
	for (int i = len - 1; i >= 0; ++ i)
		if (data[i] != t.data[i]) return data[i] < t.data[i];
	return false;
}

bool LongLongInt::operator >(const LongLongInt &t) const {
	if (len != t.len) return len > t.len;
	for (int i = len - 1; i >= 0; ++ i)
		if (data[i] != t.data[i]) return data[i] > t.data[i];
	return false;
}

bool LongLongInt::operator <=(const LongLongInt &t) const {
	return !(*this > t);
}

bool LongLongInt::operator >=(const LongLongInt &t) const {
	return !(*this < t);
}

LongLongInt LongLongInt::operator +(const LongLongInt &t) const {
	LongLongInt result;
	result.len = max(len, t.len);
	for (int i = 0; i < result.len; ++ i) {
		result.data[i] = data[i] + t.data[i];
		if (i < 127) result.data[i + 1] += result.data[i] / 10;
		result.data[i] %= 10;
	}
	if (result.len < 128 && result.data[result.len] > 0) ++ result.len;
	return result;
}

char *LongLongInt::toString() const {
	char *p = new char[len + 2];
	for (int i = len - 1; i >= 0; -- i)
		p[len - i - 1] = data[i];
	p[len] = 0;
	return p;
}

ostream &operator <<(ostream &os, const LongLongInt &t) {
	for (int i = t.len - 1; i >= 0; -- i)
		os << (char)(t.data[i] + '0');
	return os;
}
