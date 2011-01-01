// System Class
// Author: Xiao Jia
// Date: 2010/12/02

#pragma once

#include <string>
#include "Date.h"

class System
{
public:
	static Date getCurrentDate();
	static std::wstring getWorkingDirectory();
	static std::wstring getReaderFileName();
	static std::wstring getAdminFileName();
	static std::wstring getKindFileName();
	static std::wstring getBookFileName();
	static int getBorrowedBookExpiredDays();	// penalty should be counted if a borrowing is expired
	static int getReservedBookExpiredDays();	// reservations should be canceled if expired
protected:
	static void initialize();
private:
	static Date currentDate;
};
