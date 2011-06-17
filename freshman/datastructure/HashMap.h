/** @file */

#ifndef __HASHMAP_H
#define __HASHMAP_H

#include "Utility.h"
#include "LinkedList.h"

/**
 * HashMap is a map implemented by hashing. Also, the 'capacity' here means the
 * number of buckets in your inner implemention, not the current number of the
 * elements.
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
 *      HashMap<int, int, Hashint> hash;
 * @endcode
 *
 * We don't require an order in the iteration, but you should
 * guarantee all elements will be iterated.
 */
template <class K, class V, class H>
class HashMap
{
private:
	int _capacity, _size;
	LinkedList<Entry<K, V> > *storage;
public:
    class ConstIterator
    {
	private:
		int _capacity, curp;
		const LinkedList<Entry<K, V> > *storage;
		typename LinkedList<Entry<K, V> >::ConstIterator cur;
		const HashMap<K, V, H> *s;
    public:
        /**
         * Returns true if the iteration has more elements.
         * O(n) for iterating through the container.
         */
        bool hasNext() {
			if (s == NULL) return false;
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
        const Entry<K, V>& next() {
			if (s == NULL) throw ElementNotExist();
			if (s->size() == 0) throw ElementNotExist();
			if (curp == -1) {
				while (storage[++ curp].isEmpty());
				cur = storage[curp].constIterator();
				return cur.next();
			}
			if (cur.hasNext()) return cur.next();
			for (int i = curp + 1; i < _capacity; ++ i)
				if (!storage[i].isEmpty()) {
					cur = storage[i].constIterator();
					curp = i;
					return cur.next();
				}
			throw ElementNotExist();
		}
		ConstIterator(int _capacity = 0, const LinkedList<Entry<K, V> > *storage = NULL, const HashMap<K, V, H> *s = NULL)
			: _capacity(_capacity), curp(-1), storage(storage), cur(storage[0].constIterator()), s(s) {}
    };

    class Iterator
    {
	private:
		int _capacity, curp;
		LinkedList<Entry<K, V> > *storage;
		typename LinkedList<Entry<K, V> >::Iterator cur;
		HashMap<K, V, H> *s;
    public:
        /**
         * Returns true if the iteration has more elements.
         * O(n) for iterating through the container.
         */
        bool hasNext() {
			if (s == NULL) return false;
			if (s->size() == 0) return false;
			if (curp == -1) return true;
			if (cur.hasNext()) return true;
			for (int i = curp + 1; i < _capacity; ++ i)
				if (!storage[i].isEmpty())
					return true;
			return false;
		}

        /**
         * Returns a const reference the next element in the iteration.
         * O(n) for iterating through the container.
         * @throw ElementNotExist
         */
        Entry<K, V>& next() {
			if (s == NULL) throw ElementNotExist();
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
         * Removes from the underlying collection the last element returned by the iterator (optional operation).
         * O(1)
         * @throw ElementNotExist
         */
        void remove() {
			if (s == NULL) throw ElementNotExist();
			if (curp == -1) throw ElementNotExist();
			cur.remove();
			curp = -1;
			-- s->_size;
		}

		Iterator(int _capacity = 0, LinkedList<Entry<K, V> > *storage = NULL, HashMap<K, V, H> *s = NULL)
			: _capacity(_capacity), curp(-1), storage(storage), cur(storage[0].iterator()), s(s) {}
    };

	friend void Iterator::remove();

    /**
     * Constructs an empty list with an initial capacity.
     * You can construct it with your own initial capacity.
     */
    HashMap() {
		_capacity = 99971;
		_size = 0;
		storage = new LinkedList<Entry<K, V> >[_capacity];
	}

    /**
     * Copy-constructor
     */
    HashMap(const HashMap& x) {
		_capacity = 99971;
		_size = 0;
		storage = new LinkedList<Entry<K, V> >[_capacity];
		HashMap::ConstIterator it = x.iterator();
		while (it->hasNext()) {
			Entry<K, V> entry = it->next();
			put(entry.key, entry.value);
		}
	}

    /**
     * Constructs an empty HashMap with the specified initial capacity
     */
    HashMap(int initialCapacity) {
		_capacity = initialCapacity;
		_size = 0;
		storage = new LinkedList<Entry<K, V> >[_capacity];
	}

    /**
     * Constructs a new HashMap with the same mappings as the specified Map.
     */
    template <class E2>
    explicit HashMap(const E2 &m) {
		_capacity = 99971;
		_size = 0;
		storage = new LinkedList<Entry<K, V> >[_capacity];
		typename E2::ConstIterator it = m.iterator();
		while (it->hasNext()) {
			Entry<K, V> entry = it->next();
			put(entry.key, entry.value);
		}
	}

    /**
     * assignment operator
     */
    HashMap& operator=(const HashMap& x) {
		clear();
		HashMap::ConstIterator it = x.iterator();
		while (it->hasNext()) {
			Entry<K, V> entry = it->next();
			put(entry.key, entry.value);
		}
	}

    /**
     * destructor
     */
    ~HashMap() {
		delete[] storage;
	}

    /**
     * Returns an iterator over the elements in this map in proper sequence.
     */
    Iterator iterator() {
		return Iterator(_capacity, storage, this);
	}

    /**
     * Returns an CONST iterator over the elements in this map in proper sequence.
     */
    ConstIterator constIterator() const {
		return ConstIterator(_capacity, storage, this);
	}

    /**
     * Removes all of the mappings from this map.
     */
    void clear() {
		for (int i = 0; i < _capacity; ++ i)
			storage[i].clear();
		_size = 0;
	}

    /**
     * Returns true if this map contains a mapping for the specified key.
     * O(1) for average
     */
    bool containsKey(const K& key) const {
		int p = ((H::hashcode(key) % _capacity) + _capacity) % _capacity;
		typename LinkedList<Entry<K, V> >::Iterator it = storage[p].iterator();
		while (it.hasNext()) {
			if (it.next().key == key) return true;
		}
		return false;
	}

    /**
     * Returns true if this map maps one or more keys to the specified value.
     * O(n)
     */
    bool containsValue(const V& value) const {
		ConstIterator it = constIterator();
		while (it.hasNext())
			if (it.next().value == value) return true;
		return false;
	}

    /**
     * Returns a reference to the value to which the specified key is mapped.
     * O(1) for average
     * @throw ElementNotExist
     */
    V& get(const K& key) {
		int p = ((H::hashcode(key) % _capacity) + _capacity) % _capacity;
		typename LinkedList<Entry<K, V> >::Iterator it = storage[p].iterator();
		while (it.hasNext()) {
			Entry<K, V> &entry = it.next();
			if (entry.key == key) return entry.value;
		}
		throw ElementNotExist();
	}

    /**
     * Returns a const reference to the value to which the specified key is mapped.
     * O(1) for average
     * @throw ElementNotExist
     */
    const V& get(const K& key) const {
		int p = ((H::hashcode(key) % _capacity) + _capacity) % _capacity;
		typename LinkedList<Entry<K, V> >::ConstIterator it = storage[p].constIterator();
		while (it.hasNext()) {
			Entry<K, V> entry = it.next();
			if (entry.key == key) return entry.value;
		}
		throw ElementNotExist();
	}

    /**
     * Returns true if this map contains no key-value mappings.
     * O(1)
     */
    bool isEmpty() const { return _size == 0; }

    /**
     * Associates the specified value with the specified key in this map.
     * Returns the previous value, if not exist, a value returned by the default-constructor.
     * O(1)
     */
    V put(const K& key, const V& value) {
		int p = ((H::hashcode(key) % _capacity) + _capacity) % _capacity;
		typename LinkedList<Entry<K, V> >::Iterator it = storage[p].iterator();
		while (it.hasNext()) {
			Entry<K, V> &entry = it.next();
			if (entry.key == key) {
				V ret = entry.value;
				entry.value = value;
				return ret;
			}
		}
		storage[p].add(Entry<K, V>(key, value));
		++ _size;
		return V();
	}

    /**
     * Removes the mapping for the specified key from this map if present.
     * Returns the previous value.
     * O(1) for average
     * @throw ElementNotExist
     */
    V remove(const K& key) {
		int p = ((H::hashcode(key) % _capacity) + _capacity) % _capacity;
		typename LinkedList<Entry<K, V> >::Iterator it = storage[p].iterator();
		while (it.hasNext()) {
			Entry<K, V> entry = it.next();
			if (entry.key == key) {
				-- _size;
				it.remove();
				return entry.value;
			}
		}
		throw ElementNotExist();
	}

    /**
     * Returns the number of key-value mappings in this map.
     * O(1)
     */
    int size() const { return _size; }
};
#endif
