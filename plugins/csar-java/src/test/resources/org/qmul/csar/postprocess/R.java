package base;

public class R {

    public interface Interface {

        public interface Inner {

        }
    }

    public class ChildOfInterface implements Interface {
    }

    public class ChildOfInnerInterface implements Interface.Inner {
    }
}
