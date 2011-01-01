#include "Date.h"
#include <memory.h>

Date::Date(int year, int month, int day) {
	tm *timeinfo = new(tm);
	memset(timeinfo, 0, sizeof(tm));
	timeinfo->tm_year = year - 1900;
	timeinfo->tm_mon = month - 1;
	timeinfo->tm_mday = day;
	timestamp = mktime(timeinfo);
}

int Date::getYear() const { return localtime(&timestamp)->tm_year + 1900; }
int Date::getMonth() const { return localtime(&timestamp)->tm_mon + 1; }
int Date::getDay() const { return localtime(&timestamp)->tm_mday; }
void Date::operator +=(int d) { timestamp += (time_t)d * 60 * 60 * 24; }
void Date::setTime(time_t t) { timestamp = t; }
time_t Date::getTime() const { return timestamp; }
