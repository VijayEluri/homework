// Student Model
// Author: Xiao Jia
// Date: 2010/12/01

#pragma once

#include "Reader.h"

class Student : public Reader
{
public:
	Student(std::string const &username, std::string const &password)
		: Reader(username, password) {}
	virtual ~Student() {}
	
	virtual Type getType() const { return STUDENT; }
	virtual int getBorrowLimit() const { return 5; }
	
	// TODO: add whatever you need
};
