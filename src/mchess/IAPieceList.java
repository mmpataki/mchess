/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mchess;

class LNode {
    
    int index;
    public LNode prev, next;

    public LNode (
            int index,
            LNode prev,
            LNode next) {
        this.prev = prev;
        this.next = next;
        this.index = index;
    }
    
    public boolean hasNext() {
        return !(next == null);
    }

    public int getIndex() {
        return index;
    }
    
    public LNode next() {
        return next;
    }
    
}


public class IAPieceList {

    LNode[] list;
    LNode first, last;
    
    public IAPieceList(int size) {
        list = new LNode[size];
        last = first = null;
    }
    
    public void add(int index) {
        if(list[index] == null) {
            list[index] = new LNode(index, last, null);
            if(last != null)
                last.next = list[index];
            last = list[index];
        }
        if(first == null)
            first = list[index];
    }
    
    public void delete(int index) {
        LNode prev, next;
        prev = list[index].prev;
        next = list[index].next;
        if(prev != null)
            prev.next = next;
        if(next != null)
            next.prev = prev;
        if(prev == null) {
            first = list[index].next;
        }
        list[index] = null;
    }
    
    public void move(int src, int dest) {
        list[dest] = list[src];
        list[dest].index = dest;
    }
    
    public LNode getFirst() {
        return first;
    }
    
}
