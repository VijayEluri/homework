// Admin Data Access Object
// Author: Xiao Jia
// Date: 2010/12/02

#pragma once
#include <vector>
#include <string>
using namespace std;

class Admin;

class AdminDAO
{
public:
	/**
	 *	This method is called to read all Admin objects from some file.
	 *	You can get the current working directory using System::getWorkingDirectory().
	 *	You are supposed to call this method only once in ILibrary::initialize().
	 */
	static bool loadAll();
	
	/**
	 *	This method is called to write all Admin objects to some file.
	 *	You can get the current working directory using System::getWorkingDirectory().
	 *	You are supposed to call this method only once in ILibrary::finalize().
	 */
	static bool saveAll();
	static vector <Admin *> getAll();
	static Admin *searchByName(const string &);
	static bool insert(Admin *);
	static bool erase(const string &);
	
	// TODO: add whatever you need
private:
	static vector <Admin *> all;
};
