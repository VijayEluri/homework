// This file can be modified as you like.
// A unified version will be provided during the final grading test.
// Author: Xiao Jia
// Date: 2010/12/02

#include "System.h"

#include <time.h>
#include <stdlib.h>
#include <string>
#include "Date.h"

Date System::currentDate(1900, 1, 1);

Date System::getCurrentDate()
{
	static bool _initialized = false;
	if (!_initialized) initialize();
	currentDate += rand() % 30;	// this wouldn't compile if not implemented
	return currentDate;
}

std::wstring System::getWorkingDirectory()
{
	return L"./dat/";
}

std::wstring System::getReaderFileName()
{
	return L"readers.dat";
}

std::wstring System::getAdminFileName()
{
	return L"admins.dat";
}

std::wstring System::getKindFileName()
{
	return L"kinds.dat";
}

std::wstring System::getBookFileName()
{
	return L"books.dat";
}

int System::getBorrowedBookExpiredDays()
{
	return 30;
}

int System::getReservedBookExpiredDays()
{
	return 10;
}

void System::initialize()
{
	time_t rawtime;
	srand(time(&rawtime));
	tm *timeinfo = localtime(&rawtime);
	currentDate = Date(timeinfo->tm_year, timeinfo->tm_mon, timeinfo->tm_mday);
}
