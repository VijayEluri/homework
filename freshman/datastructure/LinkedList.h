/** @file */
#ifndef __LINKEDLIST_H
#define __LINKEDLIST_H

#include "Utility.h"
#include <memory.h>

/**
 * A linked list.
 *
 * The iterator iterates in the order of the elements being loaded into this list.
 */
template <class T> class LinkedList {
public:
	struct Node {
		Node *pre, *next;
		T *data;
		Node() : pre(NULL), next(NULL), data(NULL) {
		}
		Node(const T &d) : pre(NULL), next(NULL) {
			data = new T(d);
		}
		void insert(const T &d) {
			Node *add = new Node(d);
			add->pre = this;
			add->next = this->next;
			if (add->next) add->next->pre = add;
			this->next = add;
		}
		~Node() {
			if (data) delete data;
		}
	};

private:
	Node *_head, *_last;
	int _size;

public:
	class ConstIterator {
	private:
		Node *p;
	public:
		/**
		 * Returns true if the iteration has more elements.
		 * O(1).
		 */
		bool hasNext() {
			if (p == NULL) return false;
			return p->next != NULL;
		}

		/**
		 * Returns the next element in the iteration.
		 * O(1).
		 * @throw ElementNotExist exception when hasNext() == false
		 */
		const T& next() {
			if (p == NULL) throw ElementNotExist();
			if (p->next == NULL) throw ElementNotExist();
			return *((p = p->next)->data);
		}

		ConstIterator() { p = NULL; }
		ConstIterator(Node *p) : p(p) {}
	};

    class Iterator {
	private:
		Node *p;
		LinkedList<T> *s;
    public:
        /**
         * Returns true if the iteration has more elements.
         * O(1).
         */
        bool hasNext() {
			if (p == NULL) return false;
			return p->next != NULL;
		}

        /**
         * Returns the next element in the iteration.
         * O(1).
         * @throw ElementNotExist exception when hasNext() == false
         */
        T& next() {
			if (p == NULL) throw ElementNotExist();
			if (p->next == NULL) throw ElementNotExist();
			p = p->next;
			return *(p->data);
		}

        /**
         * Removes from the underlying collection the last element
         * returned by the iterator
         * O(1).
         * @throw ElementNotExist
         */
        void remove() {
			if (p == NULL || p->data == NULL) throw ElementNotExist();
			Node *anc = p->pre;
			if (p == s->_last) s->_last = anc;
			anc->next = p->next;
			if (anc->next) anc->next->pre = anc;
			delete p;
			p = NULL;
			-- s->_size;
		}

		void set(const T &elem) {
			if (p == NULL || p->data == NULL) throw ElementNotExist();
			*p->data = elem;
		}

		Iterator() { p = NULL; }
		Iterator(Node *p, LinkedList <T> *s) : p(p), s(s) {}
    };

	friend void Iterator::remove();

    /**
     * Constructs an empty list
     */
    LinkedList() {
		_head = _last = new Node();
		_head->pre = _head;
		_size = 0;
	}

    /**
     * Copy constructor
     * You may utilize the ``addAll'' function from Utility.h
     */
    LinkedList(const LinkedList<T> &c) {
		_head = _last = new Node();
		_head->pre = _head;
		_size = 0;
		addAll(*this, c);
	}

    /**
     * Assignment operator
     * You may utilize the ``addAll'' function from Utility.h
     */
    LinkedList<T>& operator=(const LinkedList<T> &c) {
		this->clear();
		addAll(*this, c);
		return *this;
	}

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * const iterator.
     * You may utilize the ``addAll'' function from Utility.h
     */
    template <class C> LinkedList(const C& c) {
		_head = _last = new Node();
		_head->_pre = _head;
		_size = 0;
		addAll(*this, c);
	}

    /**
     * Desturctor
     */
    ~LinkedList() {
		clear();
		delete _head;
	}

    /**
     * Inserts the specified element at the specified position in
     * this list.
     * O(n)
     * @throw IndexOutOfBound exception when index is out of bound
     */
    void add(int index, const T& elem) {
		Node *anc = _head;
		for (int i = 0; i < index; ++ i)
			anc = anc->next;
		anc->insert(elem);
		while (_last->next) _last = _last->next;
		++ _size;
	}

    /**
     * Appends the specified element to the end of this list.
     * O(1).
     * Always returns true;
     */
    bool add(const T& elem) {
		_last->insert(elem);
		while (_last->next) _last = _last->next;
		++ _size;
		return true;
	}

    /**
     * Inserts the specified element at the beginning of this list.
     * O(1).
     */
    void addFirst(const T& elem) {
		_head->insert(elem);
		while (_last->next) _last = _last->next;
		++ _size;
	}

    /**
     * Removes all of the elements from this list.
     * O(n).
     */
    void clear() {
		while (_head->next) {
			Iterator cur(_head, this);
			cur.next();
			cur.remove();
		}
		_last = _head;
	}

    /**
     * Returns true if this list contains the specified element.
     * O(n).
     */
    bool contains(const T& elem) const {
		Node *cur = _head;
		while (cur->next) {
			cur = cur->next;
			if (*(cur->data) == elem) return true;
		}
		return false;
	}

    /**
     * Returns a reference to the element at the specified position.
     * O(n).
     * @throw IndexOutOfBound exception when index is out of bound
     */
    T& get(int index) {
		Node *cur = _head;
		for (int i = 0; i <= index; ++ i) {
			cur = cur->next;
			if (!cur) throw IndexOutOfBound();
		}
		return *(cur->data);
	}

    /**
     * Returns a const reference to the element at the specified position.
     * O(n).
     * @throw IndexOutOfBound
     */
    const T& get(int index) const {
		Node *cur = _head;
		for (int i = 0; i <= index; ++ i) {
			cur = cur->next;
			if (!cur) throw IndexOutOfBound();
		}
		return *(cur->data);
	}

    /**
     * Returns a reference to the first element.
     * O(1).
     * @throw ElementNotExist
     */
    T& getFirst() {
		if (_head->next == NULL) throw ElementNotExist();
		return *(_head->next->data);
	}

    /**
     * Returns a const reference to the first element.
     * O(1).
     * @throw ElementNotExist
     */
    const T& getFirst() const {
		if (_head->next == NULL) throw ElementNotExist();
		return *(_head->next->data);
	}

    /**
     * Returns a reference to the last element.
     * O(1).
     * @throw ElementNotExist
     */
    T& getLast() {
		if (isEmpty()) throw ElementNotExist();
		return *(_last->data);
	}

    /**
     * Returns a const reference to the last element.
     * O(1).
     * @throw ElementNotExist
     */
    const T& getLast() const {
		if (isEmpty()) throw ElementNotExist();
		return *(_last->data);
	}

    /**
     * Returns the index of the first occurrence of the specified element
     * O(1).
     * in this list, or -1 if this list does not contain the element.
     */
    int indexOf(const T& elem) const {
		Node *cur = _head;
		int cnt = 0;
		while (cur->next != NULL) {
			cur = cur->next;
			if (*(cur->data) == elem) return cnt;
			++ cnt;
		}
		return -1;
	}

    /**
     * Returns true if this list contains no elements.
     * O(1).
	 */
    bool isEmpty() const {
		return (_head == _last);
	}

    /**
     * Returns an iterator
     * O(1).
     */
    Iterator iterator() {
		return Iterator(_head, this);
	}

    /**
     * Returns an const iterator
     * O(1).
     */
    ConstIterator constIterator() const {
		return ConstIterator(_head);
	}

    /**
     * Removes the element at the specified position in this list.
     * O(n).
     * @throw IndexOutOfBound exception when index is out of bound
     */
    T removeIndex(int index) {
		/*Iterator cur(_head, this);
		try {
			T ret;
			for (int i = 0; i <= index; ++ i)
				ret = cur.next();
			cur.remove();
			return ret;
		} catch (ElementNotExist e) {
			throw IndexOutOfBound();
		}*/
		if (index >= _size || index < 0) throw IndexOutOfBound();
		Node *cur = _head;
		for (int i = 0; i <= index; ++ i)
			cur = cur->next;
		T ret = *cur->data;
		Iterator it(cur, this);
		it.remove();
		return ret;
	}

    /**
     * Removes the first occurrence of the specified element from this
     * O(n).
     * list, if it is present.
     */
    bool remove(const T& elem) {
		Node *cur = _head;
		while (cur->next) {
			cur = cur->next;
			if (*(cur->data) == elem) {
				Iterator it(cur, this);
				it.remove();
				return true;
			}
		}
		return false;
	}

    /**
     * Removes and returns the first element from this list.
     * O(1).
     * @throw ElementNotExist
     */
    T removeFirst() {
		if (isEmpty()) throw ElementNotExist();
		Iterator cur(_head, this);
		T ret = cur.next();
		cur.remove();
		return ret;
	}

    /**
     * Removes and returns the last element from this list.
     * O(1).
     * @throw ElementNotExist
     */
    T removeLast() {
		if (isEmpty()) throw ElementNotExist();
		T ret = *(_last->data);
		Iterator cur(_last, this);
		cur.remove();
		return ret;
	}

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     * O(n).
     * @throw IndexOutOfBound exception when index is out of bound
     */
    T set(int index, const T& elem) {
		Node *cur = _head;
		for (int i = 0; i <= index; ++ i) {
			cur = cur->next;
			if (!cur) throw IndexOutOfBound();
		}
		*(cur->data) = elem;
		return elem;
	}

    /**
     * Returns the number of elements in this list.
     */
    int size() const { return _size; }

    /**
     * Returns a view of the portion of this list between the specified
     * fromIndex, inclusive, and toIndex, exclusive.
     * O(n).
     * @throw IndexOutOfBound
     */
    LinkedList<T> subList(int fromIndex, int toIndex) {
		Iterator cur(_head, this);
		try {
			LinkedList<T> ret;
			for (int i = 0; i < fromIndex; ++ i)
				cur.next();
			for (int i = fromIndex; i < toIndex; ++ i)
				ret.add(cur.next());
			return ret;
		} catch (ElementNotExist e) {
			throw IndexOutOfBound();
		}
	}
};
#endif

