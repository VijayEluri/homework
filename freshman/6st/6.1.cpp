/*****************
 * Author: MRain
 * Homework 6.1
*****************/
#include <iostream>
#include <sstream>
using namespace std;
class Time {
public:
	int hour, minute, second;
	Time(int hour = 0, int minute = 0, int second = 0)
		: hour(hour), minute(minute), second(second) {}
	bool operator ==(const Time &t) const{
		return (hour == t.hour) && (minute == t.minute) && (second == t.second);
	}
	bool operator !=(const Time &t) const { return !(*this == t); }
	bool operator <(const Time &t) const {
		if (hour != t.hour) return hour < t.hour;
		else if (minute != t.minute) return minute < t.minute;
		return second < t.second;
	}
	bool operator >(const Time &t) const {
		if (hour != t.hour) return hour > t.hour;
		else if (minute != t.minute) return minute > t.minute;
		return second > t.second;
	}
	bool operator >=(const Time &t) const {
		return !(*this < t);
	}
	bool operator <=(const Time &t) const {
		return !(*this > t);
	}
	void operator ++() {
		++ second;
		if (second == 60) {
			++ minute; second = 0;
			if (minute == 60)
				++ hour, second = 0;
		}
	}
	void operator --() {
		-- second;
		if (second < 0) {
			second = 59; -- minute;
			if (minute < 0)
				-- hour, minute = 59;
		}
	}
	void operator +=(int a) {
		second += a;
		minute += second / 60;
		second %= 60;
		hour += minute / 60;
		minute %= 60;
	}
	void operator -=(int a) {
		second -= a;
		if (second < 0) {
			minute -= (- second) / 60 + 1;
			second = second % 60 + 60;
			if (minute < 0) {
				hour -= (- minute) / 60 + 1;
				minute = minute % 60 + 60;
			}
		}
	}
	int operator -(const Time &t) const {
		int a = hour * 3600 + minute * 60 + second;
		int b = t.hour * 3600 + t.minute * 60 + t.second;
		return a - b;
	}
};

ostream &operator <<(ostream &os, const Time &t) {
	return os << t.hour << '-' << t.minute << '-' << t.second;
}
