#include "Book.h"
#include "Date.h"
#include "KindDAO.h"
#include "Reader.h"
#include "Kind.h"
#include "System.h"

Kind &Book::getKind() const { return *kind; }
void Book::setKind(const Kind &newkind) {
	kind = KindDAO::searchByISBN(newkind.getISBN());
} 

Reader *Book::getReader() const { return reader; }
void Book::setReader(Reader *newreader) {
	reader = newreader;
	borrowed = System::getCurrentDate();
}

Reader *Book::getReserver() const {
	if (!reserver) return 0;
	Date now = System::getCurrentDate();
	if (now - reserved + 1 > System::getReservedBookExpiredDays()) {
		return 0;
	}
	return reserver;
}
void Book::setReserver(Reader *newreserver) {
	reserver = newreserver;
	reserved = System::getCurrentDate();
}

Date Book::getBorrowedDate() const { return borrowed; }
Date Book::getReservedDate() const { return reserved; }

bool Book::isLentOut() const { return (reader != 0); }
int Book::getID() const { return id; }
bool Book::isAvailable() const { return available; }
