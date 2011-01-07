#include "Date.h"
#include <memory.h>

Date::Date(int year, int month, int day)
	: year(year), month(month), day(day) {
	int y = year, m = month, d = day;
	if (m > 2) m -= 3;
	else {
		m += 9;
		-- y;
	}
	int c = y / 100, ya = y - 100 * c;
	count = 1721119 + d + (146097 * c) / 4 + (1461 * ya) / 4 + (153 * m + 2) / 5;
}

Date::Date(int tot) {
	int x, y, m, d, j;
	count = tot;
	j = count - 1721119;
	y = (j * 4 - 1) / 146097;
	j = j*4 - 146097*y - 1;
	x = j/4;
	j = (x*4 + 3) / 1461;
	y = 100*y + j;
	x = (x*4) + 3 - 1461*j;
	x = (x + 4)/4;
	m = (5*x - 3)/153;
	x = 5*x - 3 - 153*m;
	d = (x + 5)/5;
	if ( m < 10 ) {
		m += 3;
	} else {
		m -= 9;
		y ++;
	}
	year = y, month = m, day = d;
}

int Date::getYear() const { return year; }
int Date::getMonth() const { return month; }
int Date::getDay() const { return day; }
int Date::getTotalDays() const { return count; }

void Date::operator += (int days) {
	*this = Date(count + days);
}

void Date::operator -= (int days) {
	*this = Date(count - days);
}

int Date::operator -(const Date &t) {
	return count - t.count;
}
