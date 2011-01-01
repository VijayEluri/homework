/*****************
 * Author: MRain
 * Homework 2.2
*****************/
#include <cstdio>
#include <cstdlib>
#include <cctype>
#include <ctime>
inline void swap(int &a, int &b) { int c = a; a = b; b = c; }

char buff[300];

int generate() {
	int type = rand() % 4;
	int a, b;
	switch (type) {
		case 0:	// plus
			a = rand() % 99 + 1;
			b = rand() % (100 - a) + 1;
			printf("%d+%d=", a, b);
			return a + b;
		case 1:	// subtract
			a = rand() % 100 + 1;
			b = rand() % 100 + 1;
			if (a < b) swap(a, b);
			printf("%d-%d=", a, b);
			return a - b;
		case 2:	// multiplus
			do {
				a = rand() % 99 + 1;
				b = rand() % 99 + 1;
			} while (a * b > 100);
			printf("%d*%d=", a, b);
			return a * b;
		case 3:	// divide
			do {
				a = rand() % 99 + 1;
				b = rand() % 99 + 1;
			} while (a * b > 100);
			printf("%d/%d=", a * b, a);
			return b;
	}
	return -1;
}

char *success[5] = {
	"Good job!",
	"Accepted",
	"You are right!",
	"Wow!Good answer!",
	"It's ok"
};

char *failed[5] = {
	"Wrong Answer",
	"Well, maybe you can try again.",
	"No!!! It can't be!",
	"Maybe you can ask your math teacher for help.",
	"bad job.."
};

int main() {
	srand(time(0));
	int result = generate();
	int got;
	scanf("%d", &got);
	if (got == result) {
		puts(success[rand() % 5]);
	} else {
		puts(failed[rand() % 5]);
	}
	return 0;
}
