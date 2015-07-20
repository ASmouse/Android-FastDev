package cn.kasogg.booster.util.http.handler;

import java.util.Map;

import cn.kasogg.booster.util.http.data.NetError;

/**
 * Author: KasoGG
 * Date:   2015-07-17 15:34
 */
public interface ResponseHandler<T> {
    public void onSuccess(T response, int statusCode, Map<String, String> headers);

    public void onFailure(NetError error);
}