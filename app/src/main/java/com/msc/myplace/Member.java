package com.msc.myplace;

import java.util.UUID;

/**
 * Created by tosh8 on 30.04.2016.
 */
public class Member {
    public String name;
    public String id;

    public Member() {

    }

    public Member(String name) {
        id = UUID.randomUUID().toString();
        this.name = name;
    }
}
