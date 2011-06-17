/** @file */
#ifndef __ARRAYLIST_H
#define __ARRAYLIST_H

#include "Utility.h"

/**
 * The ArrayList is just like vector in C++.
 * You should know that "capacity" here doesn't mean how many elements are now in this list, it means
 * the length of the array of your inner implemention
 * For example, even if the capacity is 10, the method "isEmpty()" may still return true.
 *
 * The iterator iterates in the order of the elements being loaded into this list
 */
template <class E>
class ArrayList
{
private:
	int _capacity, _size;
	E *data;
public:
    class ConstIterator
    {
	private:
		const ArrayList<E> * array;
		int p;
    public:
        /**
         * Returns true if the iteration has more elements.
         * O(1)
         */
        bool hasNext() {
			return p < array->size() - 1;
		}

        /**
         * Returns the next element in the iteration.
         * O(1)
         * @throw ElementNotExist
         */
        const E& next() {
			if (!hasNext()) throw ElementNotExist();
			return array->get(++ p);
		}

		ConstIterator(const ArrayList <E> *array, int p = -1)
			: array(array), p(p) {}
    };

    class Iterator
    {
	private:
		ArrayList<E> * array;
		int p;
    public:
        /**
         * Returns true if the iteration has more elements.
         * O(1)
         */
        bool hasNext() {
			return p < array->size() - 1;
		}

        /**
         * Returns the next element in the iteration.
         * O(1)
         * @throw ElementNotExist
         */
        E& next() {
			if (!hasNext()) throw ElementNotExist();
			return array->get(++ p);
		}

        /**
         * Removes from the underlying collection the last element returned by the iterator (optional operation).
         * O(n)
         * @throw ElementNotExist
         */
        void remove() {
			if (p == -1 || p >= array->size()) throw ElementNotExist();
			array->removeIndex(p);
			p = -1;
		}

		Iterator(ArrayList <E> *array, int p = -1)
			: array(array), p(p) {}
    };

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    ArrayList() {
		_capacity = 10;
		_size = 0;
		data = new E[_capacity];
	}

    /**
     * Constructs a list containing the elements of the specified collection, in
     * the order they are returned by the collection's iterator.
     */
    template <class E2>
    explicit ArrayList(const E2& x) {
		_capacity = x.size() * 2;
		if (_capacity < 10) _capacity = 10;
		_size = 0;
		data = new E[_capacity];
		typename E2::ConstIterator it = x.constIterator();
		while (it->hasNext())
			add(it->next());
	}

    /**
     * Constructs an empty list with the specified initial capacity.
     */
    ArrayList(int initialCapacity) {
		_capacity = initialCapacity;
		_size = 0;
		data = new E[_capacity];
	}

    /**
     * Destructor
     */
    ~ArrayList() {
		if (data) delete []data;
	}

    /**
     * Assignment operator
     */
    ArrayList& operator=(const ArrayList& x) {
		_capacity = x.size() * 2;
		if (_capacity < 10) _capacity = 10;
		delete []data;
		data = new E[_capacity];
		for (int i = 0; i < x.size(); ++ i)
			data[i] = x.get(i);
		_size = x.size();
		return *this;
	}

    /**
     * Copy-constructor
     */
    ArrayList(const ArrayList& x) {
		_capacity = x.size() * 2;
		if (_capacity < 10) _capacity = 10;
		data = new E[_capacity];
		for (int i = 0; i < x.size(); ++ i)
			data[i] = x.get(i);
		_size = x.size();
	}

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     */
    Iterator iterator() {
		return Iterator(this, -1);
	}

    /**
     * Returns an CONST iterator over the elements in this list in proper sequence.
     */
    ConstIterator constIterator() const {
		return ConstIterator(this, -1);
	}

    /**
     * Appends the specified element to the end of this list.
     * O(1)
     */
    bool add(const E& e) {
		if (_size == _capacity) {
			_capacity *= 2;
			E *renew = new E[_capacity];
			if (!renew) return false;
			for (int i = 0; i < _size; ++ i)
				renew[i] = data[i];
			delete []data;
			data = renew;
		}
		data[_size ++] = e;
		return true;
	}

    /**
     * Inserts the specified element at the specified position in this list.
     * The range of index is [0, size].
     * O(n)
     * @throw IndexOutOfBound
     */
    void add(int index, const E& element) {
		if (index > _size || index < 0) throw IndexOutOfBound();
		if (_size == _capacity) {
			_capacity *= 2;
			E *renew = new E[_capacity];
			for (int i = 0; i < _size; ++ i)
				renew[i] = data[i];
			delete []data;
			data = renew;
		}
		++ _size;
		for (int i = _size - 1; i > index; -- i)
			data[i] = data[i - 1];
		data[index] = element;
	}

    /**
     * Removes all of the elements from this list.
     */
    void clear() {
		_size = 0;
	}

    /**
     * Returns true if this list contains the specified element.
     * O(n)
     */
    bool contains(const E& e) const {
		for (int i = 0; i < _size; ++ i)
			if (data[i] == e) return true;
		return false;
	}

    /**
     * Increases the capacity of this ArrayList instance, if necessary, to ensure that it can hold at least the number of elements specified by the minimum capacity argument.
     */
    void ensureCapacity(int minCapacity) {
		if (_capacity >= minCapacity) return;
		_capacity = minCapacity;
		E *renew = new E[_capacity];
		for (int i = 0; i < _size; ++ i)
			renew[i] = data[i];
		delete []data;
		data = renew;
	}

    /**
     * Returns a reference to the element at the specified position in this list.
     * O(1)
     * @throw IndexOutOfBound
     */
    E& get(int index) {
		if (index >= _size || index < 0) throw IndexOutOfBound();
		return data[index];
	}

    /**
     * Returns a const reference to the element at the specified position in this list.
     * O(1)
     * @throw IndexOutOfBound
     */
    const E& get(int index) const {
		if (index >= _size || index < 0) throw IndexOutOfBound();
		return data[index];
	}

    /**
     * Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not contain the element.
     * O(n)
     */
    int indexOf(const E& e) const {
		for (int i = 0; i < _size; ++ i)
			if (data[i] == e) return i;
		return -1;
	}

    /**
     * Returns true if this list contains no elements.
     * O(1)
     */
    bool isEmpty() const { return _size == 0; }

    /**
     * Returns the index of the last occurrence of the specified element in this list, or -1 if this list does not contain the element.
     * O(n)
     */
    int lastIndexOf(const E& e) const {
		for (int i = _size - 1; i >= 0; -- i)
			if (data[i] == e) return i;
		return -1;
	}

    /**
     * Removes the element at the specified position in this list.
     * Returns the element that was removed from the list.
     * O(n)
     * @throw IndexOutOfBound
     */
    E removeIndex(int index) {
		if (index >= _size || index < 0) throw IndexOutOfBound();
		E ret = data[index];
		-- _size;
		for (int i = index; i < _size; ++ i)
			data[i] = data[i + 1];
		return ret;
	}

    /**
     * Removes the first occurrence of the specified element from this list, if it is present.
     * O(n)
     */
    bool remove(const E& e) {
		int index = indexOf(e);
		if (index == -1) return false;
		removeIndex(index);
		return true;
	}

    /**
     * Removes from this list all of the elements whose index is between fromIndex, inclusive, and toIndex, exclusive.
     * O(n)
     * @throw IndexOutOfBound
     */
    void removeRange(int fromIndex, int toIndex) {
		if (fromIndex < 0 || fromIndex >= _size) throw IndexOutOfBound();
		if (toIndex < fromIndex || toIndex > _size) throw IndexOutOfBound();
		int delta = toIndex - fromIndex;
		for (int i = toIndex; i < _size; ++ i)
			data[i - delta] = data[i];
		_size -= delta;
	}

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * Returns the element previously at the specified position.
     * O(1)
     * @throw IndexOutOfBound
     */
    E &set(int index, const E& element) {
		if (index < 0 || index >= _size) throw IndexOutOfBound();
		return data[index] = element;
	}

    /**
     * Returns the number of elements in this list.
     * O(1)
     */
    int size() const { return _size; }

    /**
     * Returns a view of the portion of this list between the specified fromIndex, inclusive, and toIndex, exclusive.
     * O(n)
     * @throw IndexOutOfBound
     */
    ArrayList subList(int fromIndex, int toIndex) const {
		if (fromIndex < 0 || fromIndex >= _size) throw IndexOutOfBound();
		if (toIndex < fromIndex || toIndex > _size) throw IndexOutOfBound();
		ArrayList ret((toIndex - fromIndex) * 2);
		for (int i = fromIndex; i < toIndex; ++ i)
			ret.add(data[i]);
		return ret;
	}
};
#endif

