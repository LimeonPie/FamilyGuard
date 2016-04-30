package com.msc.myplace;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tosh8 on 30.04.2016.
 */

public class Group {
    public String id;
    public String familyName;
    public List<Member> members;

    public Group(String familyName) {
        id = UUID.randomUUID().toString();
        this.familyName = familyName;
        members = new ArrayList<>(0);
    }

    public void addMember(Member member) {
        members.add(member);
    }
}
