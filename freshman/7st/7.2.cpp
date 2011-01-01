/*****************
 * Author: MRain
 * Homework 7.2
*****************/
#include <cstdio>

int main() {
	for (unsigned char c = 'A'; c <= 'Z'; ++ c)
		printf("%c (%o)8 (%d)10 (%X)16\n", c, c, c, c);
	for (unsigned char c = 'a'; c <= 'z'; ++ c)
		printf("%c (%o)8 (%d)10 (%X)16\n", c, c, c, c);
	return 0;
}
