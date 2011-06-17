/** @file */
#ifndef __TREEMAP_H
#define __TREEMAP_H

#include "Utility.h"

/**
 * A map is a sequence of (key, value) entries that provides fast retrieval
 * based on the key. At most one value is held for each key.
 *
 * TreeMap is the balanced-tree implementation of map. The iterators must
 * iterate through the map in the natural order (operator<) of the key.
 */
template<class K, class V> class TreeMap {
private:
	struct Node {
		Node *lch, *rch, *father;
		Entry<K, V> data;
		int cnt;
		Node () : data(K(), V()) { cnt = 0; }
		Node(const Entry<K, V>& data) : data(data) { cnt = 0; }
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

	Node *insert(Node *cur, Entry<K, V> &data) {
		if (cur == null) {
			cur = new Node(data);
			cur->lch = cur->rch = cur->father = null;
			cur->cnt = 1;
			data.value = V();
		} else if (data.key < cur->data.key) {
			cur->lch = insert(cur->lch, data);
			cur->lch->father = cur;
//			if (cur->rch != null)
				if (cur->lch->lch->cnt > cur->rch->cnt)
					cur = rightrotate(cur);
		} else if (data.key > cur->data.key) {
			cur->rch = insert(cur->rch, data);
			cur->rch->father = cur;
//			if (cur->lch != null)
				if (cur->rch->rch->cnt > cur->lch->cnt)
					cur = leftrotate(cur);
		} else if (data.key == cur->data.key) {
			V tmp = data.value;
			data.value = cur->data.value;
			cur->data.value = tmp;
		}
		update(cur);
		return cur;
	}
	Node *erase(Node *cur, const K &key, V &value) {
		if (cur == null) return null;
		if (key < cur->data.key)
			cur->lch = erase(cur->lch, key, value);
		else if (key > cur->data.key)
			cur->rch = erase(cur->rch, key, value);
		else if (cur->lch == null && cur->rch == null) {
			value = cur->data.value;
			delete cur;
			cur = null;
		} else {
			if (cur->lch->cnt < cur->rch->cnt) cur = leftrotate(cur);
			else cur = rightrotate(cur);
			cur = erase(cur, key, value);
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

	Node *find(const K &key) const {
		Node *cur = root;
		while (cur != null) {
			if (cur->data.key == key) return cur;
			else if (key < cur->data.key) cur = cur->lch;
			else cur = cur->rch;
		}
		return cur;
	}

	bool findvalue(Node *cur, const V &value) const {
		if (cur == null) return false;
		if (cur->data.value == value) return true;
		if (findvalue(cur->lch, value)) return true;
		if (findvalue(cur->rch, value)) return true;
		return false;
	}
public:
    class ConstIterator {
	private:
		Node *p, *null;
		bool accessed;
    public:
        /**
         * Returns true if the iteration has more elements.
         * Amortized O(1).
         */
        bool hasNext() {
			if (p == null || p == NULL) return false;
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
         * Amortized O(1).
         * @throw ElementNotExist
         */
        const Entry<K, V>& next() {
			if (p == NULL || p == null) throw ElementNotExist();
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

    class Iterator {
	private:
		Node *p, *null;
		bool accessed;
		TreeMap<K, V> *s;
    public:
        /**
         * Returns true if the iteration has more elements.
         * Amortized O(1).
         */
        bool hasNext() {
			if (p == null || p == NULL) return false;
			if (p->rch != null || !accessed) return true;
			Node *q = p, *x = q->father;
			while (x != null) {
				if (x->lch == q) return true;
				else q = x, x = q->father;
			}
			return false;
		}

        /**
         * Returns a reference to the next element in the iteration.
         * Amortized O(1).
         * @throw ElementNotExist
         */
        Entry<K, V>& next() {
			if (p == null || p == NULL) throw ElementNotExist();
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
         * Removes from the underlying collection the last element
         * returned by the iterator
         * Amortized O(1).
         * @throw ElementNotExist
         */
        void remove() {
			if (p == null || p == NULL) throw ElementNotExist();
			if (!s->containsKey(p->data->key)) throw ElementNotExist();
			s->remove(p->key);
			p = null;
		}

		Iterator(Node *p = NULL, Node *null = NULL, TreeMap<K, V> *s = NULL)
			: p(p), null(null), s(s) { accessed = false; }
    };

    /**
     * Constructs an empty map
     */
    TreeMap() {
		null = new Node();
		null->father = null->lch = null->rch = null;
		root = null;
	}

    /**
     * Copy constructor
     */
    TreeMap(const TreeMap &c) {
		null = new Node();
		null->father = null->lch = null->rch = null;
		root = null;
		typename TreeMap<K, V>::ConstIterator it = c.constIterator();
		while (it->hasNext()) {
			Entry<K, V> d = it->next();
			put(d.key, d.value);
		}
	}

    /**
     * Destructor
     */
    ~TreeMap() {
		clear();
		delete null;
	}

    /**
     * Assignment operator
     */
    TreeMap& operator=(const TreeMap &c) {
		clear();
		typename TreeMap<K, V>::ConstIterator it = c.constIterator();
		while (it->hasNext()) {
			Entry<K, V> d = it->next();
			put(d.key, d.value);
		}
	}

    /**
     * Constructs a new tree map containing the same mappings as the
     * given map
     */
    template <class C> TreeMap(const C& c) {
		null = new Node();
		null->father = null->lch = null->rch = null;
		root = null;
		typename C::ConstIterator it = c.constIterator();
		while (it->hasNext()) {
			Entry<K, V> d = it->next();
			put(d.key, d.value);
		}
	}

    /**
     * Returns an iterator over the elements in this map.
     * O(1).
     */
    Iterator iterator() {
		Node *p = root;
		while (p->lch != null) p = p->lch;
		return Iterator(p, null, this);
	}

    /**
     * Returns an const iterator over the elements in this map.
     * O(1).
     */
    ConstIterator constIterator() const {
		Node *p = root;
		while (p->lch != null) p = p->lch;
		return ConstIterator(p, null);
	}

    /**
     * Removes all of the mappings from this map.
     * O(n).
     */
    void clear() {
		root = allerase(root);
		//while (root != null)
		//	root = erase(root, root->data.key);
	}

    /**
     * Returns true if this map contains a mapping for the specified key.
     * O(logn).
     */
    bool containsKey(const K& key) const {
		return find(key) != null;
	}

    /**
     * Returns true if this map contains a mapping for the specified value.
     * O(n).
     */
    bool containsValue(const V& value) const {
		return findvalue(root, value);
	}

    /**
     * Returns a key-value mapping associated with the least key in
     * this map.
     * O(logn).
     * @throw ElementNotExist
     */
    const Entry<K, V>& firstEntry() const {
		if (root == null) throw ElementNotExist();
		Node *cur = root;
		while (cur->lch != null) cur = cur->lch;
		return cur->data;
	}

    /**
     * Returns the first (lowest) key currently in this map.
     * O(logn).
     * @throw ElementNotExist
     */
    const K& firstKey() const {
		return firstEntry().key;
	}

    /**
     * Returns a reference to the value which the specified key is mapped
     * O(logn).
     * @throw ElementNotExist
     */
    V& get(const K& key) {
		Node *cur = find(key);
		if (cur == null) throw ElementNotExist();
		return cur->data.value;
	}

    /**
     * Returns a reference to the value which the specified key is mapped
     * O(logn).
     * @throw ElementNotExist
     */
    const V& get(const K& key) const {
		Node *cur = find(key);
		if (cur == null) throw ElementNotExist();
		return cur->data.value;
	}

    /**
     * Returns a key-value mapping associated with the greatest key
     * in this map.
     * O(logn).
     * @throw ElementNotExist
     */
    const Entry<K, V>& lastEntry() const {
		Node *cur = root;
		while (cur->rch != null) cur = cur->rch;
		return cur->data;
	}

    /**
     * Returns the last (highest) key currently in this map.
     * O(logn).
     * @throw ElementNotExist
     */
    const K& lastKey() const {
		return lastEntry().key;
	}

    /**
     * Associates the specified value with the specified key in this map.
     * Returns the previous value, if not exist, a value returned by the
     * default-constructor.
     * O(logn).
     */
    V put(const K& key, const V& value) {
		/*Node *cur = find(key);
		if (cur != null) {
			V ret = cur->data.value;
			cur->data.value = value;
			return ret;
		} else {*/
		Entry<K, V> p(key, value);
		root = insert(root, p);
		return p.value;
		/*}*/
	}

    /**
     * Removes the mapping for this key from this TreeMap if present.
     * O(logn).
     * @throw ElementNotExist
     */
    V remove(const K& key) {
		V ret;
		int pre = root->cnt;
		root = erase(root, key, ret);
		if (pre == root->cnt) throw ElementNotExist();
		return ret;
	}

    /**
     * Returns the number of key-value mappings in this map.
     * O(logn).
     */
    int size() const { return root->cnt; }

	bool isEmpty() const { return root == null; }
};

#endif

