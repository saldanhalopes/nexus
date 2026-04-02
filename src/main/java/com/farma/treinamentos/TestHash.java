package com.farma.treinamentos;

import com.farma.treinamentos.util.HashUtil;

public class TestHash {
    public static void main(String[] args) {
        String[] passwords = {"password", "admin123", "123456", "admin", "senha123", "teste123"};
        for (String pwd : passwords) {
            String hash = HashUtil.hashPassword(pwd);
            System.out.println(pwd + ": " + hash);
        }
    }
}
