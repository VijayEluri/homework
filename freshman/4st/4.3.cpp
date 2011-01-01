#include <cstdio>
#include <cstring>
#include <cstdlib>

char *__prefix = "label";
int __label_number = 0;

void SetLabel(const char *str) {
	__prefix = (char *)malloc(strlen(str) + 10);
	strcpy(__prefix, str);
}

void SetInitNumber(int d) {
	__label_number = d;
}

char * GetNextLabel() {
	char *p = (char *)malloc(strlen(__prefix) + 20);
	sprintf(p, "%s%d", __prefix, __label_number ++);
	return p;
}
