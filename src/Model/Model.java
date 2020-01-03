package Model;

import Client.Client;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Model {

    private boolean modelSet = false;
    private ArrayList<String> messages;
    private List<Client> observers = new LinkedList<>();


    public void addObserver(Client observer) {
        this.observers.add(observer);
    }


    private synchronized void notifyObservers(ArrayList<String> messages){
        for (Client observer: observers) {
            observer.update(this, messages);
        }
    }

    public void setModel(ArrayList<String> messages) {
        this.messages = messages;
        this.modelSet = true;

        if (messages.size() != 0) {
            notifyObservers(messages);
        }

    }

    public boolean isModelSet() {
        return modelSet;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void addMessage(String msg) {
        messages.add(msg);
        notifyObservers(messages);
    }

}
