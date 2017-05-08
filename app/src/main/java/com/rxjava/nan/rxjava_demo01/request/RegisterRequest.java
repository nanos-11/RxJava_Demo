package com.rxjava.nan.rxjava_demo01.request;

import java.io.Serializable;

/**
 * Author: Season(ssseasonnn@gmail.com)
 * Date: 2016/12/6
 * Time: 11:31
 * FIXME
 */
public class RegisterRequest implements Serializable {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RegisterRequest(String id) {
        this.id = id;
    }
}
