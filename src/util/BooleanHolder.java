package util;
/**
 * BooleanHolder holds a boolean value and has some methods that are used with MenuItems Object, Builder,
 * and other menu-related classes.
 * @author Keenan Nicholson
 *
 */
public class BooleanHolder extends Holder {
    private boolean val;

    /**
     * Create a new BooleanHolder with the default value of false.
     */
    public BooleanHolder() {
        val = false;
    }

    /**
     * Create a new BooleanHolder with the default value of b.
     * @param b the initial value
     */
    public BooleanHolder(boolean b) {
        val = b;
    }

    /**
     * Sets this BooleanHolder to a value of b
     * @param b the value to set this BooleanHolder to
     */
    public void setValue(boolean bol) {
        this.val = bol;
        changed();
    }

    /**
     * Gets this BooleanHolder value
     * @return this BooleanHolder value
     */
    public boolean getValue() {
        return this.val;
    }

    public String toString() {
        return ((val) ? "true" : "false");
    }

    @Override
    public void setRawVariable(Object o) {
        val = (Boolean) o;
        changed();
    }

    @Override
    public Object getRawVariable() {
        return new Boolean(val);
    }

    @Override
    public void setToHolder(Holder h) {
        if (!(h instanceof BooleanHolder))
            throw new RuntimeException(
                    "A BooleanHolder can only be set to values of other BooleanHolders.");
        setValue((Boolean) h.getRawVariable());
        changed();
    }

    @Override
    public Holder clone() {
        return new BooleanHolder(val);
    }
}
