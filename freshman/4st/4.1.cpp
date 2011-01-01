#include <cstdio>

int gcd(int a, int b) {
	return b == 0 ? a : gcd(b, a % b);
}

struct rationalT{
	int num, den;
	rationalT(int num = 0, int den = 1) : num(num), den(den) {
		this->normalize();
	}
	void normalize() {
		int k = gcd(num, den);
		num /= k;
		den /= k;
	}
};

rationalT CreateRational(int num, int den = 1) {
	rationalT x(num, den);
	x.normalize();
	return x;
}

rationalT AddRational(rationalT r1, rationalT r2) {
	rationalT x(r1.num * r2.den + r2.num * r1.den, r1.den * r2.den);
	x.normalize();
	return x;
}

rationalT MultiplyRational(rationalT r1, rationalT r2) {
	rationalT x(r1.num * r2.num, r1.den * r2.den);
	x.normalize();
	return x;
}

float GetRational(rationalT r) {
	return (float)r.num / (float)r.den;
}

void PrintRational(rationalT r) {
	printf("%d/%d", r.num, r.den);
}
