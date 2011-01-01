/*****************
 * Author: MRain
 * Homework 3.1
*****************/
#include <cstdio>
#include <cmath>
double Eps, x;

int main() {
	printf("Please input x: ");
	scanf("%lf", &x);
	printf("Please input epsion: ");
	scanf("%lf", &Eps);
	double ans = 0, val = 1;
	for (int i = 1; ; ++ i) {
		if (i & 1) ans += val;
		else ans -= val;
		val *= x / (2.0 * i) * x / (2.0 * i + 1);
		if (val < Eps) break;
	}
	printf("%.10f\n", ans);
	return 0;
}
