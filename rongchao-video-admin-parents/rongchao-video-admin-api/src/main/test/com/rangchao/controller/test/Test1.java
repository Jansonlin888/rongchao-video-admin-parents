package com.rangchao.controller.test;

import com.rongchao.utils.MD5Utils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;

import java.awt.*;
public class Test1 {
    @Test
    public void testPsw() {
        try {
            String str = MD5Utils.getMD5Str("a123456");
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
