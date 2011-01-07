#include "User.h"

string User::getUsername() const { return username; }
string User::getPassword() const { return password; }
void User::setPassword(const string &newpassword) {
	password = newpassword;
}
