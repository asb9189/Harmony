package Network;

import java.io.Serializable;

public class Request<E extends Serializable> implements Serializable {

    private RequestType type;
    private E data;


    public enum RequestType{
        ERROR,
        SEND_MESSAGE,
        CHANGE,
        REQUEST_MODEL,
        MODEL
    }

    public Request(RequestType type, E data) {

        this.type = type;
        this.data = data;

    }

    public RequestType getType() {
        return type;
    }

    public E getData() {
        return data;
    }


}
