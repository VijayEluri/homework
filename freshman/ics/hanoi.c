#include <stdio.h>
#include <stdlib.h>
int n, i;
int block[3][20], size[3];
int width;

void putblank(int c) {
	int i;
	for (i = 0; i < c; ++ i) putchar(' ');
}

void putblock(int x, int t) {
	if (block[t][x] > 0) {
		int size = block[t][x];
		int i;
		putblank(n - size);
		for (i = 0; i < size * 2 + 1; ++ i)
			putchar('@');
		putblank(n - size);
	} else {
		putblank(n);
		putchar('|');
		putblank(n);
	}
}

void print_state() {
	int i;
	putblank(n + 1);
	putchar('|');
	putblank(2 * n + 1);
	putchar('|');
	putblank(2 * n + 1);
	puts("|");
	for (i = n - 1; i >= 0; -- i) {
		putchar(' ');
		putblock(i, 0);
		putchar(' ');
		putblock(i, 1);
		putchar(' ');
		putblock(i, 2);
		puts("");
	}
	puts("");
}

void move(int src, int dst) {
	getchar();
	int sz = block[src][size[src] - 1];
	block[dst][size[dst] ++] = sz;
	block[src][-- size[src]] = 0;
	system("clear");
	printf("After move block %d from %c to %c:\n", sz, 'A' + src, 'A' + dst);
	print_state();
}

void work(int n, int src, int dst) {
	if (n == 0) return;
	work(n - 1, src, 3 - dst - src);
	move(src, dst);
	work(n - 1, 3 - dst - src, dst);
}

int main(int argc, char *argv[]) {
	if (argc != 2) {
		puts("USAGE:\t./hanoi {num}");
		puts("\tnum is the number of blocks in Hanoi tower.");
		return 0;
	}
	n = atoi(argv[1]);
	width = ((n * 2) + 1) * 3 + 4;
	//init
	size[0] = n; size[1] = size[2] = 0;
	for (i = 0; i < n; ++ i) block[0][i] = n - i;
	
	system("clear");
	puts("First we have:");
	print_state();
	work(n, 0, 2);
	puts("DONE!");
	return 0;
}
