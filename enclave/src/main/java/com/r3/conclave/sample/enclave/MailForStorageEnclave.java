package com.r3.conclave.sample.enclave;

import com.r3.conclave.enclave.Enclave;
import com.r3.conclave.enclave.EnclavePostOffice;
import com.r3.conclave.mail.EnclaveMail;
import com.r3.conclave.mail.PostOffice;
import com.r3.conclave.utilities.internal.UtilsKt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MailForStorageEnclave extends Enclave {
    byte[] plainText = "".getBytes();

    private EnclavePostOffice selfPostOffice;

    private EnclavePostOffice getSelfPostOffice() {
        if (selfPostOffice == null) {
            selfPostOffice = postOffice(getEnclaveInstanceInfo());
        }
        return selfPostOffice;
    }

    private static String reverse(String input) {
        StringBuilder builder = new StringBuilder(input.length());
        for (int i = input.length() - 1; i >= 0; i--)
            builder.append(input.charAt(i));
        return builder.toString();
    }

    private byte[] encrypt(byte[] input) {
        byte[] bytes = getSelfPostOffice().encryptMail(input);
        return bytes;
    }

    @Override
    protected byte[] receiveFromUntrustedHost(byte[] bytes) {
        if(bytes.length == 0){
            return plainText;
        }
        return encrypt(bytes);
    }

    @Override
    protected void receiveMail(long id, EnclaveMail mail, String routingHint) {
        plainText = mail.getBodyAsBytes();
    }
}
