// Date Class
// Author: Xiao Jia
// Date: 2010/12/01

#include <ctime>
#pragma once

class Date
{
public:
	Date(int year, int month, int day);
	Date(int);
	
	int getYear() const;
	int getMonth() const;
	int getDay() const;
	int getTotalDays() const;
	void operator +=(int);
	void operator -=(int);
	int operator -(const Date &);
	
	// TODO: add whatever you need
private:
	int year, month, day;
	int count;
};
