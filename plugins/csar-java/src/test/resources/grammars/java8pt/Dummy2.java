package grammars.java8pt;

class Dummy {
    public void dummy() {
        //Calling method defined by interface
        RequestListener listener = new ActionHandler2(); /* ActionHandler2 can be represented as RequestListener... */
        listener.requestReceived(); /* ...and thus is known to implement requestReceived() method */
    }
}
