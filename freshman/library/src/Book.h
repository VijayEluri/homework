// Book Model
// Author: Xiao Jia
// Date: 2010/12/03

#pragma once

#include "Date.h"
class Kind;
class Reader;

class Book
{
public:
	Book(int id, Kind *kind, Reader *reader, Reader *reserver, const Date &borrowed, const Date &reserved, bool avail)
		: id(id), kind(kind), reader(reader), reserver(reserver), borrowed(borrowed), reserved(reserved), available(avail) {}
	virtual ~Book() {}
	
	Kind &getKind() const;
	void setKind(Kind const &);
	
	Reader *getReader() const;
	void setReader(Reader *);

	Reader *getReserver() const;
	void setReserver(Reader *);
	
	Date getBorrowedDate() const;
	Date getReservedDate() const;
	
	/**
	 *	Return whether this book is available to borrow or not.
	 *	Return true if this book is available, false otherwise.
	 */
	bool isAvailable() const;
	
	/**
	 *	Return whether this book has been lent out or not.
	 *	Return true if this book has been lent out, false otherwise.
	 */
	bool isLentOut() const;
	
	// TODO: add whatever you need
	int getID() const;
	bool available;
private:
	int id;
	Kind *kind;
	Reader *reader, *reserver;
	Date borrowed, reserved;
};
