package org.starlight.netty;

import lombok.Builder;
import lombok.Data;

@Data
public class User {
    private int state;//用户状态，0在线，1下线
    private String username;
}
