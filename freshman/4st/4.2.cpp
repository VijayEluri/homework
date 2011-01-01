/*****************
 * Author: MRain
 * Homework 4.2
 * Task: Use a array to solve the Josephus Problem
*****************/
#include <cstdio>
#include <cstring>
#include <vector>
using namespace std;
#define MAXNUM 1000 + 10

// n stands for the number of people
// ptr stands for the current people
// step shows how many times should be count
int n, m, ptr, step;
// leave[i] stands for if the i-th people has left
bool leave[MAXNUM];
// list stands for the order people left
vector <int> list;

int main() {
	scanf("%d%d", &n, &m);
	memset(leave, 0, sizeof(leave));
	list.clear();
	ptr = 0;
	for (int i = 0; i < n; ++ i) {
		// move ptr to the first one who still in the game
		while (leave[ptr]) ptr = (ptr + 1) % n;

		step = m;
		while (-- step) {
			do {
				ptr = (ptr + 1) % n;
			} while (leave[ptr]);
		}

		// ptr leave the game
		leave[ptr] = true;
		list.push_back(ptr + 1);
	}
	for (int i = 0; i < (int)list.size(); ++ i) {
		if (i) putchar(' ');
		printf("%d", list[i]);
	}
	printf("\n");
	return 0;
}
