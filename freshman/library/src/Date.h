// Date Class
// Author: Xiao Jia
// Date: 2010/12/01

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


#include <string>
#include <vector>
using namespace std;

int compare(const wstring&, const wstring&);
wstring trim(wstring);
vector <wstring> split(wstring);

typedef enum {O_READ, O_WRITE} DT_MODE;
class DataToken {
private:
	DT_MODE O_MODE;
	bool active;
	int fd;

public:
	DataToken();
	DataToken(const wstring&, DT_MODE);
	~DataToken();
	bool close();
	void *read();
	bool open(const wstring&, DT_MODE);
	bool write(const void *, size_t);
};
