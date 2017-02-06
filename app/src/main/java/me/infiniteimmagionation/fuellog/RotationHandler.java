package me.infiniteimmagionation.fuellog;

/**
 * Created by milor on 6.2.2017..
 */

public class RotationHandler {

    public static enum classOption{
        Main, LastN, Edit, Add, None;

    }

    private static classOption currentClass;

    public static classOption getInstance()
    {
        if (currentClass == null) {
            currentClass = classOption.None;
        }
        return currentClass;
    }

    public static void setClass(classOption option){
        currentClass=option;
    }

    public static classOption getCurrentClass(){
        return currentClass;
    }
}
