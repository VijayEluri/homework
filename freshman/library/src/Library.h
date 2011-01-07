// Library Class
// Author: Xiao Jia
// Date: 2010/12/02

#pragma once

#include "ILibrary.h"

/**
 *	See ILibrary.h for documentation.
 */
class Library : public ILibrary
{
public:
	// TODO: add whatever you need
	~Library();
	bool initialize();
	void finalize();
	Reader *readerLogin(std::string const &username, std::string const &password);
	Admin *adminLogin(std::string const &username, std::string const &password);
	
	std::vector<Kind *> searchByISBN(std::string const &isbn);
	std::vector<Kind *> searchByName(std::wstring const &name);
	std::vector<Kind *> searchByAuthor(std::wstring const &author);
	std::vector<Kind *> searchByIndex(std::wstring const &index);

	std::vector<Kind *> searchByExpression(std::wstring const &expr);
	std::vector<Kind *> searchLikeName(std::wstring const &name);


	void reorderResults(std::vector<Kind *> &kinds, ReorderType reorderType = ORDER_BY_NAME, bool descending = false);
};
