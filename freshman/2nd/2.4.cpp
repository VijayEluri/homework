/*****************
 * Author: MRain
 * Homework 2.4
*****************/
#include <cstdio>
#include <cstring>

int mark[1001];

int main() {
	memset(mark, 0, sizeof(mark));
	int now = 2;
	// strictly follow the rule
	while (now <= 1000) {
		mark[now] = 1;		// prime
		for (int j = 2; now * j <= 1000; ++ j)
			mark[now * j] = 2;	//not prime
		while(now <= 1000 && mark[now]) ++ now;
	}
	for (int i = 2; i <= 1000; ++ i)
		printf("%d: %s\n", i, (mark[i] == 1 ? "Prime" : "Not prime"));
	return 0;
}
