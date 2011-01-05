// Book Data Access Object
// Author: Xiao Jia
// Date: 2010/12/02

#include "basesystem"
#include "System.h"
#include "KindDAO.h"
#include "ReaderDAO.h"
#include "Book.h"
#include "Reader.h"
#pragma once

class BookDAO {
public:
	/**
	 *	This method is called to read all Book objects from some file.
	 *	You can get the current working directory using System::getWorkingDirectory().
	 *	You are supposed to call this method only once in ILibrary::initialize().
	 */
	static bool loadAll();
	
	/**
	 *	This method is called to write all Book objects to some file.
	 *	You can get the current working directory using System::getWorkingDirectory().
	 *	You are supposed to call this method only once in ILibrary::finalize().
	 */
	static bool saveAll();
	static vector <Book *> getAll();
	static int getNextBookID();
	static Book *searchByID(int);
private:
	static vector <Book *> all;
	static int nextID;
	
	// TODO: add whatever you need
};
