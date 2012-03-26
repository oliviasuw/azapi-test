package bgu.dcr.az.cpu.server.utils;

public class AzArrays {

    /**
     * concatanate two arrays and return a new array that contain both their element 
     * @param <T>
     * @param left
     * @param right
     * @return 
     */
    @SafeVarargs
	public static <T> T[] concatanate(T[] left, T... right){
        int nsize = left.length + right.length;
        @SuppressWarnings("unchecked")
		T[] a = (T[])java.lang.reflect.Array.newInstance(left.getClass().getComponentType(), nsize);
        System.arraycopy(left, 0, a, 0, left.length);
        System.arraycopy(right, 0, a, left.length, right.length);
        return a;
    }
}
