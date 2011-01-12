// Reader Interface
// Author: Xiao Jia
// Date: 2010/12/02

#pragma once

#include <vector>
#include <string>
#include "User.h"
using namespace std;

class BookDAO;
class Book;

class Reader : public User {
public:
	Reader(std::string const &username, std::string const &password);
	virtual ~Reader() {}
	
	enum Type { STUDENT, TEACHER };
	virtual Type getType() const = 0;
	virtual int getBorrowLimit() const = 0;
	
	bool borrow(Book &);
	bool returnBook(Book &);
	bool renew(Book &);
	bool reserve(Book &);
	std::vector<Book *> getBorrowedBooks();
	std::vector<Book *> getReservedBooks();
	int getPenalty();

	friend class BookDAO;
private:
	vector <Book *> borrowed;
	vector <Book *> reserved;
};
