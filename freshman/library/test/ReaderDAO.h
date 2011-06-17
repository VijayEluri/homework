// Reader Data Access Object
// Author: Xiao Jia
// Date: 2010/12/02

#pragma once
#include <vector>
#include <string>
using namespace std;
class Reader;

class ReaderDAO
{
public:
	/**
	 *	This method is called to read all Reader objects from some file.
	 *	You can get the current working directory using System::getWorkingDirectory().
	 *	You are supposed to call this method only once in ILibrary::initialize().
	 */
	static bool loadAll();
	
	/**
	 *	This method is called to write all Reader objects to some file.
	 *	You can get the current working directory using System::getWorkingDirectory().
	 *	You are supposed to call this method only once in ILibrary::finalize().
	 */
	static bool saveAll();
	
	// TODO: add whatever you need
	static vector <Reader *> getAll();
	static bool insert(Reader *);
	static bool erase(const string &);
	static Reader *searchByName(const string &);
private:
	static vector <Reader *> all;
};
