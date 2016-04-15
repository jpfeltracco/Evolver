package controllers;

public interface LimitedControllers {

    /**
     * This variable states the amount of controllers of this type that have been checked out.
     */
    public static int controllersCheckedOut = 0;

    /**
     * Initialize this controller type. IE: Make external connections to hardware, initialize values, etc.
     */
    public void initializeControllerType();

    /**
     * Gets the number of total controllers allowed by this Controller type.
     * @return the max number of controllers that can be used
     */
    public abstract int getMaxNumberOfControllers();

    /**
     * Checks out a given number of controllers as a Controller[]. This gives the Controller
     * class the ability to distribute controllers properly. NOTE: May not be able to checkout
     * enough controllers to fill the whole request, in which just the remaining controllers
     * will be returned.
     * @param preferedNumControllers the number of Controllers requested
     * @return the Controllers that have been checked out.
     */
    public Controller[] checkout(int preferedNumControllers);
}
