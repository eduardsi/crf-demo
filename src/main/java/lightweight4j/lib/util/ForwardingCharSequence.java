package lightweight4j.lib.util;

import javax.annotation.Nonnull;

public abstract class ForwardingCharSequence implements CharSequence {

    @Override
    public int length() {
        return delegate().length();
    }

    @Override
    public char charAt(int index) {
        return delegate().charAt(index);
    }

    @Nonnull
    @Override
    public String toString() {
        return delegate().toString();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return delegate().subSequence(start, end);
    }

    protected abstract CharSequence delegate();
}
