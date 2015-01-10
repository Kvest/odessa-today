package com.kvest.odessatoday.io.network.response;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 22:54
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseResponse<T> {
    public static final int SUCCESS_CODE = 0;

    public int code;
    public T data;
    public String error;

    public boolean isSuccessful() {
        return (code == SUCCESS_CODE);
    }
}
