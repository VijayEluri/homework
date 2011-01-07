// Library Interface
// Author: Xiao Jia
// Date: 2010/12/03

#pragma once

#include <vector>
#include <string>

class Reader;
class Admin;
class Kind;

class ILibrary {
public:
	virtual ~ILibrary() {}
	
	/**
	 *	This method is called when the system starts up and will not be called again.
	 *	Any other methods or functions will not be called before this method.
	 *	Implement this method with some initializations such as loading data from files.
	 *	Return true if the system is initialized successfully. Otherwise, return false.
	 */
	virtual bool initialize() = 0;
	
	/**
	 *	This method is called when the system shuts down and will not be called again.
	 *	This method will not be called before initialize().
	 *	Implement this method with some initializations such as releasing allocated memory.
	 */
	virtual void finalize() = 0;
	
	/**
	 *	This method is called to log in the system as a reader using a pair of username and password.
	 *	Return the pointer to a loaded Reader object if the pair matched successfully.
	 *	Otherwise, return NULL to indicate error.
	 */
	virtual Reader *readerLogin(std::string const &username, std::string const &password) = 0;
	
	/**
	 *	This method is called to log in the system as an administrator using a pair of username and password.
	 *	Return the pointer to a loaded Admin object if the pair matched successfully.
	 *	Otherwise, return NULL to indicate error.
	 */
	virtual Admin *adminLogin(std::string const &username, std::string const &password) = 0;
	
	virtual std::vector<Kind *> searchByISBN(std::string const &isbn) = 0;
	virtual std::vector<Kind *> searchByName(std::wstring const &name) = 0;
	virtual std::vector<Kind *> searchByAuthor(std::wstring const &author) = 0;
	virtual std::vector<Kind *> searchByIndex(std::wstring const &index) = 0;
	
	virtual std::vector<Kind *> searchByExpression(std::wstring const &expr) = 0;
	virtual std::vector<Kind *> searchLikeName(std::wstring const &name) = 0;
	
	enum ReorderType { ORDER_BY_ISBN, ORDER_BY_NAME, ORDER_BY_INDEX, ORDER_BY_COPY_COUNT };
	virtual void reorderResults(std::vector<Kind *> &kinds, ReorderType reorderType = ORDER_BY_NAME, bool descending = false) = 0;
};
