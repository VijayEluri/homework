// Admin Model
// Author: Xiao Jia
// Date: 2010/12/03

#pragma once

#include <vector>
#include <string>
#include "User.h"

class Reader;
class Admin;
class Kind;
class Book;

class Admin : public User
{
public:
	virtual ~Admin();
	
	/**
	 *	Construct an Admin object using the pair of username and password provided.
	 */
	Admin(std::string const &username, std::string const &password);

	/**
	 *	This method is called to create a new object as provided.
	 *	Return true if the new object is created successfully.
	 *	Otherwise, return false and the corresponding DAO should not be changed.
	 */
	bool createReader(std::string const &username, std::string const &password, Reader::Type);
	
	/**
	 *	This method is called to remove an existing object as provided.
	 *	Return true if the object is removed successfully.
	 *	Otherwise, return false and the corresponding DAO should not be changed.
	 */
	bool removeReader(std::string const &username);
	
	/**
	 *	This method is called to create a new object as provided.
	 *	Return true if the new object is created successfully.
	 *	Otherwise, return false and the corresponding DAO should not be changed.
	 */
	bool createAdmin(std::string const &username, std::string const &password);
	
	/**
	 *	This method is called to remove an existing object as provided.
	 *	Return true if the object is removed successfully.
	 *	Otherwise, return false and the corresponding DAO should not be changed.
	 */
	bool removeAdmin(std::string const &username);
	
	/**
	 *	This method is called to create a new object as provided.
	 *	Return true if the new object is created successfully.
	 *	Otherwise, return false and the corresponding DAO should not be changed.
	 */
	bool createKind(std::string const &isbn, std::wstring const &name, std::wstring const &authors, std::wstring const &index);
	
	/**
	 *	This method is called to remove an existing object as provided.
	 *	Return true if the object is removed successfully.
	 *	Otherwise, return false and the corresponding DAO should not be changed.
	 */
	bool removeKind(std::string const &isbn);
	
	/**
	 *	This method is called to create a new object as provided.
	 *	Return true if the new object is created successfully.
	 *	Otherwise, return false and the corresponding DAO should not be changed.
	 */
	bool createBook(Kind const &, bool borrowable);
	
	/**
	 *	This method is called to remove an existing object as provided.
	 *	Return true if the object is removed successfully.
	 *	Otherwise, return false and the corresponding DAO should not be changed.
	 */
	bool removeBook(Book const &);
	
	/**
	 *	This method is called to get all Reader objects in the system.
	 *	Return a list of the objects.
	 */
	std::vector<Reader const *> getAllReaders();
	
	/**
	 *	This method is called to get all Admin objects in the system.
	 *	Return a list of the objects.
	 */
	std::vector<Admin const *> getAllAdmins();
	
	/**
	 *	This method is called to find a Reader object with the name provided.
	 *	Return the pointer to a loaded object if successfully founded.
	 *	Otherwise, return NULL to indicate error.
	 */
	Reader *getReaderByName(std::wstring const &name);
	
	/**
	 *	This method is called to find an Admin object with the name provided.
	 *	Return the pointer to a loaded object if successfully founded.
	 *	Otherwise, return NULL to indicate error.
	 */
	Admin *getAdminByName(std::wstring const &name);
};
