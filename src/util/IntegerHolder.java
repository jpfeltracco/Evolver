package util;
/**
 * IntegerHolder holds a integer value and has some methods that are used with MenuItems Object, Builder,
 * and other menu-related classes.
 * @author Keenan Nicholson
 *
 */
public class IntegerHolder extends Holder {
    private int val = 0;

    /**
     * Create a new IntegerHolder with the default value of 0.
     */
    public IntegerHolder() {
        val = 0;
    }

    /**
     * Create a new IntegerHolder with the default value of i.
     * @param i the initial value
     */
    public IntegerHolder(int i) {
        val = i;
    }

    /**
     * Sets this IntegerHolder to a value of i
     * @param i the value to set this IntegerHolder to
     */
    public void setValue(int i) {
        this.val = i;
        changed();
    }

    /**
     * Gets this IntegerHolder value
     * @return this IntegerHolder value
     */
    public int getValue() {
        return this.val;
    }

    public String toString() {
        return "" + val;
    }

    /**
     * Gets this IntegerHolder value
     * @return this IntegerHolder value
     */
    public int getInt() {
        return val;
    }

    @Override
    public void setRawVariable(Object o) {
        val = (Integer) o;
        changed();
    }

    @Override
    public Object getRawVariable() {
        return new Integer(val);
    }

    @Override
    public void setToHolder(Holder h) {
        if (!(h instanceof IntegerHolder))
            throw new RuntimeException(
                    "A IntegerHolder can only be set to values of other IntegerHolders.");
        setValue((Integer) h.getRawVariable());
        changed();
    }

    @Override
    public Holder clone() {
        return new IntegerHolder(val);
    }
}
