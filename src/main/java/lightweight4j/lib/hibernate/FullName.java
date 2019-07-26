package lightweight4j.lib.hibernate;

import lightweight4j.lib.util.ForwardingCharSequence;

public class FullName extends ForwardingCharSequence {

    private final String fullName;

    public FullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    protected CharSequence delegate() {
        return fullName;
    }


    public static void main(String[] args) {
        var x = new FullName("hello") + "";
        System.out.println(x);
    }
}


