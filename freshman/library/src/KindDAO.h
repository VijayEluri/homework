// Kind Data Access Object
// Author: Xiao Jia
// Date: 2010/12/02

#include "basesystem"
#include "System.h"
#include "Kind.h"
#pragma once

class Kind;

class KindDAO {
public:
	/**
	 *	This method is called to read all Kind objects from some file.
	 *	You can get the current working directory using System::getWorkingDirectory().
	 *	You are supposed to call this method only once in ILibrary::initialize().
	 */
	static bool loadAll();
	
	/**
	 *	This method is called to write all Kind objects to some file.
	 *	You can get the current working directory using System::getWorkingDirectory().
	 *	You are supposed to call this method only once in ILibrary::finalize().
	 */
	static bool saveAll();
	static vector <Kind *> getAll();
	static bool insert(Kind*);
	static bool erase(const string&);
	static Kind *searchByISBN(const string &);
	static vector <Kind *>searchByAuthor(const wstring &);
	static vector <Kind *>searchByIndex(const wstring &);
	static vector <Kind *>searchByName(const wstring &);
	
	// TODO: add whatever you need
private:
	static vector <Kind *> all;
	static map <wstring, set<Kind *> > mapByAuthor;
	static map <wstring, set<Kind *> > mapByIndex;
	static map <wstring, set<Kind *> > mapByName;
	static map <string, Kind *> mapByISBN;
};
