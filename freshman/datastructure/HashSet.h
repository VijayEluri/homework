/** @file */
#ifndef __HASHSET_H
#define __HASHSET_H

#include "Utility.h"
#include "LinkedList.h"

/**
 * A HashSet holds elements using a hash table, providing facilities
 * to insert, remove an element into the container and search an
 * element within the container efficiently.
 *
 * We don't require an order in the iteration, but you should
 * guarantee all elements will be iterated.
 *
 * Template argument H are used to specify the hash function.
 * H should be a class with a static function named ``hashcode'',
 * which takes a parameter of type T and returns a value of type int.
 * For example, the following class
 * @code
 *      class Hashint {
 *      public:
 *          static int hashcode(int obj) {
 *              return obj;
 *          }
 *      };
 * @endcode
 * specifies an hash function for integers. Then we can define:
 * @code
 *      HashSet<int, Hashint> hash;
 * @endcode
 */
template <class T, class H> class HashSet {
private:
	int _capacity, _size;
	LinkedList<T> *storage;

public:
    class ConstIterator {
	private:
		int _capacity, curp;
		LinkedList<T> *storage;
		typename LinkedList<T>::ConstIterator cur;
		HashSet<T, H> *s;
    public:
        /**
         * Returns true if the iteration has more elements.
         * O(n) for iterating through the container
         */
        bool hasNext() {
			if (s->size() == 0) return false;
			if (curp == -1) return true;
			if (cur.hasNext()) return true;
			for (int i = curp + 1; i < _capacity; ++ i)
				if (!storage[i].isEmpty())
					return true;
			return false;
		}

        /**
         * Returns the next element in the iteration.
         * O(n) for iterating through the container.
         * @throw ElementNotExist
         */
        const T& next() {
			if (s->size() == 0) throw ElementNotExist();
			if (curp == -1) {
				while (storage[++ curp].isEmpty());
				cur = storage[curp].iterator();
				return cur.next();
			}
			if (cur.hasNext()) return cur.next();
			for (int i = curp + 1; i < _capacity; ++ i)
				if (!storage[i].isEmpty()) {
					cur = storage.iterator();
					curp = i;
					return cur.next();
				}
			throw ElementNotExist();
		}

		ConstIterator(int _capacity, LinkedList<T> *storage, HashSet<T, H> *s)
			: _capacity(_capacity), curp(-1), storage(storage), cur(storage[0].iterator()), s(s) {}
    };

    class Iterator {
	private:
		int _capacity, curp;
		LinkedList<T> *storage;
		typename LinkedList<T>::Iterator cur;
		HashSet<T, H> *s;
    public:
        /**
         * Returns true if the iteration has more elements.
         * O(n) for iterating through the container.
         */
        bool hasNext() {
			if (s->size() == 0) return false;
			if (curp == -1) return true;
			if (cur.hasNext()) return true;
			for (int i = curp + 1; i < _capacity; ++ i)
				if (!storage[i].isEmpty())
					return true;
			return false;
		}

        /**
         * Returns the next element in the iteration.
         * O(n) for iterating through the container
         * @throw ElementNotExist
         */
        const T& next() {
			if (s->size() == 0) throw ElementNotExist();
			if (curp == -1) {
				while (storage[++ curp].isEmpty());
				cur = storage[curp].iterator();
				return cur.next();
			}
			if (cur.hasNext()) return cur.next();
			for (int i = curp + 1; i < _capacity; ++ i)
				if (!storage[i].isEmpty()) {
					cur = storage[i].iterator();
					curp = i;
					return cur.next();
				}
			throw ElementNotExist();
		}

        /**
         * Removes from the underlying collection the last element
         * returned by the iterator.
         * O(1)
         * @throw ElementNotExist
         */
        void remove() {
			if (curp == -1) throw ElementNotExist();
			cur.remove();
			curp = -1;
			-- s->_size;
		}

		Iterator(int _capacity, LinkedList<T> *storage, HashSet<T, H> *s)
			: _capacity(_capacity), curp(-1), storage(storage), cur(storage[0].iterator()), s(s) {}
    };
	friend void Iterator::remove();

    /**
     * Constructs a empty set with your own default capacity
     */
    HashSet() {
		_capacity = 99971;
		_size = 0;
		storage = new LinkedList<T>[_capacity];
	}

    /**
     * Destructor
     */
    ~HashSet() {
		delete[] storage;
	}

    /**
     * Copy constructor
     */
    HashSet(const HashSet &c) {
		_capacity = 99971;
		_size = 0;
		storage = new LinkedList<T>[_capacity];
		HashSet::ConstIterator it = c.iterator();
		while (it->hasNext())
			add(it->next());
	}

    /**
     * Assignment operator
     */
    HashSet& operator=(const HashSet &c) {
		clear();
		HashSet::ConstIterator it = c.iterator();
		while (it->hasNext())
			add(it->next());
	}

    /**
     * Constructs a new set containing the elements in the specified
     * collection.
     */
    template<class C> explicit HashSet(const C& c) {
		_capacity = 99971;
		_size = 0;
		storage = new LinkedList<T>[_capacity];
		typename C::ConstIterator it = c.iterator();
		while (it->hasNext())
			add(it->next());
	}

    /**
     * Constructs a new, empty set; the backing HashMap instance has the
     * specified capacity
     */
    HashSet(int capacity) {
		_capacity = capacity;
		_size = 0;
		storage = new LinkedList<T>[_capacity];
	}

    /**
     * Adds the specified element to this set if it is not already present.
     * Returns false if element is previously in the set.
     * O(1) for average
     */
    bool add(const T& elem) {
		int p = ((H::hashcode(elem) % _capacity) + _capacity) % _capacity;
		if (storage[p].contains(elem)) return false;
		else storage[p].add(elem), ++ _size;
		return true;
	}

    /**
     * Removes all of the elements from this set.
     */
    void clear() {
		for (int i = 0; i < _capacity; ++ i)
			storage[i].clear();
		_size = 0;
	}

    /**
     * Returns true if this set contains the specified element.
     * O(1) for average
     */
    bool contains(const T& elem) const {
		int p = ((H::hashcode(elem) % _capacity) + _capacity) % _capacity;
		return storage[p].contains(elem);
	}

    /**
     * Returns true if this set contains no elements.
     * O(1)
     */
    bool isEmpty() const {
		return _size == 0;
	}

    /**
     * Returns an iterator over the elements in this set.
     */
    Iterator iterator() {
		return Iterator(_capacity, storage, this);
	}

    /**
     * Returns an const iterator over the elements in this set.
     */
    ConstIterator constIterator() const {
		return ConstIterator(_capacity, storage, this);
	}

    /**
     * Removes the specified element from this set if it is present.
     * O(1) for average
     */
    bool remove(const T& elem) {
		int p = ((H::hashcode(elem) % _capacity) + _capacity) % _capacity;
		if (!storage[p].remove(elem)) return false;
		-- _size;
		return true;
	}

    /**
     * Returns the number of elements in this set (its cardinality).
     * O(1)
     */
    int size() const {
		return _size;
	}
};

#endif

