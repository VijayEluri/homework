// Date Class
// Author: Xiao Jia
// Date: 2010/12/01

#include <ctime>
#pragma once

class Date
{
public:
	Date(int year, int month, int day);
	Date(time_t x) : timestamp(x) {}
	
	int getYear() const;
	int getMonth() const;
	int getDay() const;
	time_t getTime() const;
	void setTime(time_t);
	void operator +=(int);
	
	// TODO: add whatever you need
private:
	time_t timestamp;
};
