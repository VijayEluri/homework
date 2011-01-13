// User Interface
// Author: Xiao Jia
// Date: 2010/12/01

#pragma once

#include <string>
using namespace std;

class User {
public:
	User(std::string const &username, std::string const &password)
		: username(username), password(password) {}
	virtual ~User() {};
	
	virtual std::string getUsername() const;
	virtual std::string getPassword() const;
	virtual void setPassword(std::string const &);

protected:
	string username, password;
};
