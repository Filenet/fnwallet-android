package io.filenet.wallet.utils;

import android.text.TextUtils;

public class PrivateKeyValidator {
    public static boolean isPrivateKey(String key){
        if(TextUtils.isEmpty(key) || key.length() != 64){
            return false;
        }
        for(int i = 0; i < key.length(); i++){
            char k = key.charAt(i);
            if(k < '0' || k > '9' && k < 'A' || k >'F' && k < 'a' || k > 'f') {
                return false;
            }
        }
        return true;
    }
}
