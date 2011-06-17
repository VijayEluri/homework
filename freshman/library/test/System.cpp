#include "System.h"
#include <time.h>
#include <stdlib.h>
#include <string>
#include "Date.h"

Date System::currentDate(1900, 1, 1);

Date System::getCurrentDate()
{
	return currentDate;
}

void System::setCurrentDate(Date d)
{
	currentDate = d;
}

static void copy_file(std::string const &from, std::string const &to)
{
	::system(("cp /Y \"" + from + "\" \"" + to + "\" > .silent").c_str());
}

void System::prepare()
{
	static const std::string dir = "./dat/";
	static const std::string frm = "../src/dat/";
	copy_file(frm + "readers.dat", dir + "readers.dat");
	copy_file(frm + "admins.dat", dir + "admins.dat");
	copy_file(frm + "kinds.dat", dir + "kinds.dat");
	copy_file(frm + "books.dat", dir + "books.dat");
}

std::wstring System::getWorkingDirectory()
{
	return L"./dat/"
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

