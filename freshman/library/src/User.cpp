#include "User.h"

User::User(const string &username, const string &password)
	: username(username), password(password) {}

string User::getUsername() const { return username; }
string User::getPassword() const { return password; }
void User::setPassword(const string &newpassword) {
	password = newpassword;
}
