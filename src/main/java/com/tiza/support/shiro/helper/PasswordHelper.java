package com.tiza.support.shiro.helper;

import com.tiza.web.sys.dto.SysUser;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * Description: PasswordHelper
 * Author: DIYILIU
 * Update: 2017-11-24 09:52
 */

public class PasswordHelper {
    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

    private String hashAlgorithmName = "md5";
    private int hashIterations = 2;

    public void encryptPassword(SysUser user) {
        user.setSalt(randomNumberGenerator.nextBytes().toHex());

        String newPassword = new SimpleHash(
                hashAlgorithmName,
                user.getPassword(),
                ByteSource.Util.bytes(user.getCredentialsSalt()),
                hashIterations).toHex();

        user.setPassword(newPassword);
    }

    public String encryptPassword(String username, String password, String salt){
        return new SimpleHash(
                hashAlgorithmName,
                password,
                ByteSource.Util.bytes(username + salt),
                hashIterations).toHex();
    }


    public void setHashAlgorithmName(String hashAlgorithmName) {
        this.hashAlgorithmName = hashAlgorithmName;
    }

    public void setHashIterations(int hashIterations) {
        this.hashIterations = hashIterations;
    }
}
