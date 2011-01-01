// Reader Interface
// Author: Xiao Jia
// Date: 2010/12/02

#pragma once

#include <vector>
#include <string>
#include "User.h"

class Book;

class Reader : public User {
public:
	Reader(std::string const &username, std::string const &password);
	virtual ~Reader();
	
	enum Type { STUDENT, TEACHER };
	virtual Type getType() const = 0;
	virtual int getBorrowLimit() const = 0;
	
	virtual bool borrow(Book &) = 0;
	virtual bool returnBook(Book &) = 0;
	virtual bool renew(Book &) = 0;
	virtual bool reserve(Book &) = 0;
	virtual std::vector<Book *> getBorrowedBooks() = 0;
	virtual std::vector<Book *> getReservedBooks() = 0;
	virtual int getPenalty() = 0;
};
