class Dummy {
    public void dummy() {
        //Calling method defined by interface
        RequestListener listener = new ActionHandler(); /*ActionHandler can be
                                           represented as RequestListener...*/
        listener.requestReceived(); /*...and thus is known to implement
                                    requestReceived() method*/
    }
}
