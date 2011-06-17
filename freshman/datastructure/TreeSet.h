/** @file */

#ifndef __TREESET_H
#define __TREESET_H

#include "Utility.h"
#include <ctime>
#include <cstdlib>

/**
 * A set implemented by balanced tree,
 * the elements being putted must guarantee operator'<'
 *
 * The iterator must iterates in the order defined by the operator'<' (from the smallest to the biggest)
 */

template <class E>
class TreeSet
{
private:
	struct Node {
		Node *lch, *rch, *father;
		E data;
		int cnt;
		Node () { cnt = 0; }
		Node(const E& data) : data(data) { cnt = 0; }
	};

	Node *root, *null;

	void update(Node *cur) {
		if (cur == null) cur->cnt = 0;
		else cur->cnt = cur->lch->cnt + cur->rch->cnt + 1;
	}

	Node *leftrotate(Node *cur) {
		Node *father = cur->father;
		Node *tmp = cur->rch;
		if (father != null) {
			if (father->lch == cur) father->lch = tmp;
			else father->rch = tmp;
		}
		tmp->father = father;
		cur->rch = tmp->lch;
		if (cur->rch != null) cur->rch->father = cur;
		tmp->lch = cur;
		cur->father = tmp;
		update(cur); update(tmp);
		return tmp;
	}

	Node *rightrotate(Node *cur) {
		Node *father = cur->father;
		Node *tmp = cur->lch;
		if (father != null) {
			if (father->lch == cur) father->lch = tmp;
			else father->rch = tmp;
		}
		tmp->father = father;
		cur->lch = tmp->rch;
		if (cur->lch != null) cur->lch->father = cur;
		tmp->rch = cur;
		cur->father = tmp;
		update(cur); update(tmp);
		return tmp;
	}

	Node *insert(Node *cur, const E &data) {
		if (cur == null) {
			cur = new Node(data);
			cur->lch = cur->rch = cur->father = null;
			cur->cnt = 1;
		} else if (data < cur->data) {
			cur->lch = insert(cur->lch, data);
			cur->lch->father = cur;
//			if (cur->rch != null)
				if (cur->lch->lch->cnt > cur->rch->cnt)
					cur = rightrotate(cur);
		} else if (data > cur->data) {
			cur->rch = insert(cur->rch, data);
			cur->rch->father = cur;
//			if (cur->lch != null)
				if (cur->rch->rch->cnt > cur->lch->cnt)
					cur = leftrotate(cur);
		}
		update(cur);
		return cur;
	}
	Node *erase(Node *cur, const E &data) {
		if (data < cur->data)
			cur->lch = erase(cur->lch, data);
		else if (data > cur->data)
			cur->rch = erase(cur->rch, data);
		else if (cur->lch == null && cur->rch == null) {
			delete cur;
			cur = null;
		} else {
			if (cur->lch->cnt < cur->rch->cnt) cur = leftrotate(cur);
			else cur = rightrotate(cur);
			cur = erase(cur, data);
		}
		update(cur);
		return cur;
	}

	Node *allerase(Node *cur) {
		if (cur == null) return null;
		cur->lch = allerase(cur->lch);
		cur->rch = allerase(cur->rch);
		delete cur;
		return null;
	}

public:
    class ConstIterator
    {
	private:
		Node *p, *null;
		bool accessed;
    public:
        /**
         * Returns true if the iteration has more elements.
         * O(logn)
         */
        bool hasNext() {
			if (p == null) return false;
			if (p->rch != null || !accessed) return true;
			Node *q = p, *x = q->father;
			while (x != null) {
				if (x->lch == q) return true;
				else q = x, x = q->father;
			}
			return false;
		}

        /**
         * Returns a const reference to the next element in the iteration.
         * O(logn)
         * @throw ElementNotExist
         */
        const E& next() {
			if (p == null) throw ElementNotExist();
			if (!accessed) {
				accessed = true;
				return p->data;
			}
			if (p->rch != null) {
				p = p->rch;
				while (p->lch != null) p = p->lch;
				return p->data;
			}
			Node *q = p, *x = q->father;
			while (x != null) {
				if (x->lch == q) return (p = x)->data;
				else q = x, x = q->father;
			}
			throw ElementNotExist();
		}
		ConstIterator(Node *p = NULL, Node *null = NULL)
			: p(p), null(null) { accessed = false; }
    };

    class Iterator
    {
	private:
		Node *p, *null;
		bool accessed;
		TreeSet<E> *s;
    public:
        /**
         * Returns true if the iteration has more elements.
         * O(logn)
         */
        bool hasNext() {
			if (p == null) return false;
			if (p->rch != null || !accessed) return true;
			Node *q = p, *x = q->father;
			while (x != null) {
				if (x->lch == q) return true;
				else q = x, x = q->father;
			}
			return false;
		}

        /**
         * Returns a const reference the next element in the iteration.
         * O(logn)
         * @throw ElementNotExist
         */
        const E& next() {
			if (p == null) throw ElementNotExist();
			if (!accessed) {
				accessed = true;
				return p->data;
			}
			if (p->rch != null) {
				p = p->rch;
				while (p->lch != null) p = p->lch;
				return p->data;
			}
			Node *q = p, *x = q->father;
			while (x != null) {
				if (x->lch == q) return (p = x)->data;
				else q = x, x = q->father;
			}
			throw ElementNotExist();
		}

        /**
         * Removes from the underlying collection the last element returned by the iterator (optional operation).
         * O(logn)
         * @throw ElementNotExist
         */
        void remove() {
			if (!accessed) throw ElementNotExist();
			if (p == null) throw ElementNotExist();
			if (!s->remove(p->data)) throw ElementNotExist();
			p = null;
		}

		Iterator(Node *p, Node *null, TreeSet<E> *s)
			: p(p), null(null), s(s) { accessed = false; }
    };

    /**
     * Constructs a new, empty tree set, sorted according to the natural ordering of its elements.
     */
    TreeSet() {
		null = new Node();
		null->lch = null->rch = null->father = null;
		root = null;
	}

    /**
     * Constructs a set containing the elements of the specified collection, in
     * the order they are returned by the collection's iterator.
     */
    template <class E2>
    explicit TreeSet(const E2& x) {
		null = new Node();
		null->lch = null->rch = null->father = null;
		root = null;
		typename E2::ConstIterator it = x.constIterator();
		while (it->hasNext())
			add(it->next());
	}

    /**
     * Destructor
     */
    ~TreeSet() {
		clear();
		delete null;
	}

    /**
     * Assignment operator
     */
    TreeSet& operator=(const TreeSet& x) {
		clear();
		TreeSet<E>::ConstIterator it = x.constIterator();
		while (it->hasNext())
			add(it->next());
		return *this;
	}

    /**
     * Copy-constructor
     */
    TreeSet(const TreeSet& x) {
		null = new Node();
		null->lch = null->rch = null->father = null;
		root = null;
		TreeSet<E>::ConstIterator it = x.constIterator();
		while (it->hasNext())
			add(it->next());
	}

    /**
     * Returns an iterator over the elements in this set in proper sequence.
     */
    Iterator iterator() {
		Node *p = root;
		while (p->lch != null) p = p->lch;
		return Iterator(p, null, this);
	}

    /**
     * Returns an CONST iterator over the elements in this set in proper sequence.
     */
    ConstIterator constIterator() const {
		Node *p = root;
		while (p->lch != null) p = p->lch;
		return ConstIterator(p, null);
	}

    /**
     * Adds the specified element to this set if it is not already present.
     * Returns true if this set did not already contain the specified element.
     * O(logn)
     */
    bool add(const E& e) {
		int psize = root->cnt;
		root = insert(root, e);
		return psize > root->cnt;
	}

    /**
     * Removes all of the elements from this set.
     */
    void clear() {
		root = allerase(root);
	}

    /**
     * Returns true if this set contains the specified element.
     * O(logn)
     */
    bool contains(const E& e) const {
		Node *cur = root;
		while (cur != null) {
			if (e == cur->data) return true;
			else if (e < cur->data) cur = cur->lch;
			else cur = cur->rch;
		}
		return false;
	}

    /**
     * Returns a const reference to the first (lowest) element currently in this set.
     * O(logn)
     * @throw ElementNotExist
     */
    const E& first() const {
		if (isEmpty()) throw ElementNotExist();
		Node *cur = root;
		while (cur->lch != null) cur = cur->lch;
		return cur->data;
	}

    /**
     * Returns true if this set contains no elements.
     * O(1)
     */
    bool isEmpty() const { return root == null; }

    /**
     * Returns a const reference to the last (highest) element currently in this set.
     * O(logn)
     * @throw ElementNotExist
     */
    const E& last() const {
		if (isEmpty()) throw ElementNotExist();
		Node *cur = root;
		while (cur->rch != null) cur = cur->rch;
		return cur->data;
	}

    /**
     * Removes the specified element from this set if it is present.
     * O(logn)
     */
    bool remove(const E& e) {
		if (!contains(e)) return false;
		root = erase(root, e);
		return true;
	}

    /**
     * Returns the number of elements in this set (its cardinality).
     * O(1)
     */
    int size() const { return root->cnt; }
};
#endif
