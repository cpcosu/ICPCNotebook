# include <bits/stdc++.h>
using namespace std;
typedef vector<int> vi;
const int inf = 1e9;
// A lazy segment tree supporting range add, range set, and range get max
struct Node {
    Node *l = 0, *r = 0;
    int lo, hi, mset = inf, madd = 0, val = -inf;
    Node(int lo,int hi):lo(lo),hi(hi){} // Large interval of -inf
    // Initialize based on the values in the vector v.
    // main will call this with Node(v, 0, v.size())
    Node(vi& v, int lo, int hi) : lo(lo), hi(hi) {
        if (lo + 1 < hi) {
            int mid = lo + (hi - lo)/2;
            l = new Node(v, lo, mid); r = new Node(v, mid, hi);
            val = max(l->val, r->val);
        }
        else val = v[lo];
    }
    // query [L, R)
    int query(int L, int R) {
        if (R <= lo || hi <= L) return -inf;
        if (L <= lo && hi <= R) return val;
        push();
        return max(l->query(L, R), r->query(L, R));
    }
    // set all elements in [L, R) to x
    void set(int L, int R, int x) {
        if (R <= lo || hi <= L) return;
        if (L <= lo && hi <= R) {
            // Update the range [lo, hi) to x
            mset = val = x, madd = 0;
        }
        else {
            push(), l->set(L, R, x), r->set(L, R, x);
            val = max(l->val, r->val);
        }
    }
    // add x to all elements in [L, R)
    void add(int L, int R, int x) {
        if (R <= lo || hi <= L) return;
        if (L <= lo && hi <= R) {
            // Add x to all elements in the range [lo, hi)
            if (mset != inf) mset += x;
            else madd += x;
            val += x;
        }
        else {
            push(), l->add(L, R, x), r->add(L, R, x);
            val = max(l->val, r->val);
        }
    }
    // Push the lazily stored values.
    void push() {
        if (!l) {
            int mid = lo + (hi - lo)/2;
            l = new Node(lo, mid); r = new Node(mid, hi);
        }
        if (mset != inf)
            l->set(lo,hi,mset), r->set(lo,hi,mset), mset = inf;
        else if (madd)
            l->add(lo,hi,madd), r->add(lo,hi,madd), madd = 0;
    }
};