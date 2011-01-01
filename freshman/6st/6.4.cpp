/*****************
 * Author: MRain
 * Homework 6.4
*****************/
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <algorithm>
#include <iostream>
#include <sstream>
#include <cctype>
using namespace std;

class LongLongInt {
public:
	char data[128];
	int len;
	LongLongInt();
	LongLongInt(const char *);
	LongLongInt(int);
	bool operator ==(const LongLongInt&) const;
	bool operator !=(const LongLongInt&) const;
	bool operator <(const LongLongInt&) const;
	bool operator <=(const LongLongInt&) const;
	bool operator >(const LongLongInt&) const;
	bool operator >=(const LongLongInt&) const;
	LongLongInt operator +(const LongLongInt&) const;
	LongLongInt operator -(const LongLongInt&) const;
	char *toString() const;
};

class signed_longlongint : public LongLongInt {
public:
	bool sign;
	signed_longlongint();
	signed_longlongint(const char *);
	signed_longlongint(int);
	signed_longlongint(const LongLongInt&);
	bool operator ==(const signed_longlongint&) const;
	bool operator !=(const signed_longlongint&) const;
	bool operator <=(const signed_longlongint&) const;
	bool operator >=(const signed_longlongint&) const;
	bool operator <(const signed_longlongint&) const;
	bool operator >(const signed_longlongint&) const;
	signed_longlongint operator +(const signed_longlongint&) const;
};

signed_longlongint::signed_longlongint() : LongLongInt(), sign(false) {}
signed_longlongint::signed_longlongint(const char *str)
	: LongLongInt(isdigit(*str) ? str : str + 1),
	  sign(*str == '-') {}
signed_longlongint::signed_longlongint(int x)
	: LongLongInt(x > 0 ? x : -x), sign(x < 0) {}
signed_longlongint::signed_longlongint(const LongLongInt &t)
	: LongLongInt(t), sign(false) {}

bool signed_longlongint::operator ==(const signed_longlongint &t) const {
	return sign == t.sign && (LongLongInt)*this == (LongLongInt)t;
}

bool signed_longlongint::operator !=(const signed_longlongint &t) const {
	return !(*this == t);
}

bool signed_longlongint::operator <=(const signed_longlongint &t) const {
	if (sign == t.sign) {
		if (sign) return (LongLongInt)*this >= (LongLongInt)t;
		else return (LongLongInt)*this <= (LongLongInt)t;
	} else return sign > t.sign;
}
bool signed_longlongint::operator >=(const signed_longlongint &t) const {
	if (sign == t.sign) {
		if (sign) return (LongLongInt)*this <= (LongLongInt)t;
		else return (LongLongInt)*this >= (LongLongInt)t;
	} else return sign > t.sign;
}
bool signed_longlongint::operator <(const signed_longlongint &t) const {
	if (sign == t.sign) {
		if (sign) return (LongLongInt)*this > (LongLongInt)t;
		else return (LongLongInt)*this < (LongLongInt)t;
	} else return sign > t.sign;
}
bool signed_longlongint::operator >(const signed_longlongint &t) const {
	if (sign == t.sign) {
		if (sign) return (LongLongInt)*this < (LongLongInt)t;
		else return (LongLongInt)*this > (LongLongInt)t;
	} else return sign > t.sign;
}
signed_longlongint signed_longlongint::operator +(const signed_longlongint &t) const {
	signed_longlongint result;
	if (sign == t.sign) {
		result = (LongLongInt)*this + (LongLongInt)t;
		result.sign = sign;
	} else {
		if ((LongLongInt)*this < (LongLongInt)t) {
			result = (LongLongInt)t - (LongLongInt)*this;
			result.sign = t.sign;
		} else {
			result = (LongLongInt)*this - (LongLongInt)t;
			result.sign = sign;
		}
	}
	return result;
}

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

LongLongInt LongLongInt::operator -(const LongLongInt &t) const {
	LongLongInt result = *this;
	result.len = max(len, t.len);
	for (int i = 0; i < result.len; ++ i) {
		result.data[i] -= t.data[i];
		if (result.data[i] < 0) {
			result.data[i] += 10;
			result.data[i + 1] -= 1;
		}
	}
	if (result.len && result.data[result.len - 1] == 0) -- result.len;
	if (!result.len) result.len = 1;
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

ostream &operator <<(ostream &os, const signed_longlongint &t) {
	return os << (t.sign ? "-" : "") << (LongLongInt)t;
}

