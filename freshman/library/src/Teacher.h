// Teacher Model
// Author: Xiao Jia
// Date: 2010/12/01

#pragma once

#include "Reader.h"

class Teacher : public Reader
{
public:
	Teacher(std::string const &username, std::string const &password);
	virtual ~Teacher();
	
	virtual Type getType() const { return TEACHER; }
	virtual int getBorrowLimit() const { return 10; }
	
	// TODO: add whatever you need
};
