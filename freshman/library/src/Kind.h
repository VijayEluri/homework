// Kind Model
// Author: Xiao Jia
// Date: 2010/12/03

#pragma once

#include "basesystem"
#include <string>
#include <vector>

class KindDAO;
class Book;

class Kind
{
public:
	Kind(string const &isbn, wstring const &name, wstring const &authors, wstring const &index);
	virtual ~Kind();
	
	string getISBN() const;
	wstring getName() const;
	vector<wstring> getAuthors() const;
	wstring getAuthorstr() const;
	wstring getIndex() const;

	void setISBN(string const &);
	void setName(wstring const &);
	void setAuthors(wstring const &);
	void setIndex(wstring const &);
	
	size_t countBooks() const;
	vector<Book *> getBooks() const;

	vector <Book *> all;
private:
	wstring name, index, authors;
	string isbn;
};
