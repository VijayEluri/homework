#pragma once
#include <string>
#include <vector>
using namespace std;

int compare(const wstring&, const wstring&);
wstring trim(wstring);
vector <wstring> split(wstring);

typedef enum {O_READ, O_WRITE} DT_MODE;
class DataToken {
private:
	DT_MODE O_MODE;
	bool active;
	int fd;

public:
	DataToken();
	DataToken(const wstring&, DT_MODE);
	~DataToken();
	bool close();
	void *read();
	bool open(const wstring&, DT_MODE);
	bool write(const void *, size_t);
};
