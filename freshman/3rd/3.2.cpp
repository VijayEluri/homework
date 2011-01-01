/*****************
 * Author: MRain
 * Homework 3.2
*****************/
template <class T>
void swap(T &a, T &b) {
	T c = a;
	a = b;
	b = c;
}

template <class Iterator>
void bubblesort(Iterator begin, Iterator end) {
	for (Iterator i = begin; i != end; ++ i)
		for (Iterator j = i; j != begin && *j < *(j - 1); -- j)
			swap(*j, *(j - 1));

	// I think next one is better
	/*for (Iterator i = begin; i != end; ++ i)
		for (Iterator j = begin; j != i; ++ j)
			if (*i < *j) swap(*i, *j);*/
}

template <class Iterator, class Compare>
void bubblesort(Iterator begin, Iterator end, Compare cmp) {
	for (Iterator i = begin; i != end; ++ i)
		for (Iterator j = i; j != begin && cmp(*j, *(j - 1)); -- j)
			swap(*j, *(j - 1));
	/*for (Iterator i = begin; i != end; ++ i)
		for (Iterator j = begin; j != i; ++ j)
			if (cmp(*i, *j)) swap(*i, *j);*/
}

