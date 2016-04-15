package util;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Holder implements Serializable {

    transient ArrayList<ChangeEvent> listeners = new ArrayList<ChangeEvent>(5);

    /**
     * Sets the Holder's variable to that of another's. Object based.
     * @param o the object to set the Holder's object to
     */
    public abstract void setRawVariable(Object o);

    /**
     * Gets the Holder's variable in Object form.
     * @return the variable in Object form
     */
    public abstract Object getRawVariable();

    /**
     * Sets this Holder's values to another Holder's.
     * @param h the other Holder to set values to
     */
    public abstract void setToHolder(Holder h);

    /**
     * Clones a holder and returns one that has the same values, but is not
     * dependent on the same values.
     * @return a cloned Holder
     */
    public abstract Holder clone();

    /**
     * Add an event listener to this Holder.
     * @param c the ChangeEvent to add to this Holder
     */
    public void addListener(ChangeEvent c) {
        listeners.add(c);
    }

    protected void changed() {
        for (ChangeEvent e : listeners) {
            e.handle(getRawVariable());
        }
    }
}
