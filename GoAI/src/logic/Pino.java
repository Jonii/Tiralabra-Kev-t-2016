/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;
/**
 *
 * @author jphanski
 */
class Alkio<T> {
    private Alkio next;
    private T data;

    public Alkio getNext() {
        return next;
    }

    public void setNext(Alkio next) {
        this.next = next;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    
    
}
public class Pino<T> {
    private Alkio first;
    
    public Pino() {
        
    }
    
    public T pop() {
        T palautus = (T) first.getData();
        first = first.getNext();
        return palautus;
    }
    public void add(T uusi) {
        Alkio<T> uusiAlkio = new Alkio<>();
        uusiAlkio.setData(uusi);
        uusiAlkio.setNext(first);
        first = uusiAlkio;
    }
}
