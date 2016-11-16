package local.watt.mzansitravel.Interfaces;

/**
 * Created by f4720431 on 2016/09/19.
 */
public interface OnTaskCompleted<E> {
    public void onTaskCompleted(E event);
    public void onError();
}
