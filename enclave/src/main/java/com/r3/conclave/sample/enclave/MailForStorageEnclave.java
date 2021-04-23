package com.r3.conclave.sample.enclave;

import com.r3.conclave.enclave.Enclave;
import com.r3.conclave.enclave.EnclavePostOffice;
import com.r3.conclave.mail.EnclaveMail;
import com.r3.conclave.mail.PostOffice;
import com.r3.conclave.utilities.internal.UtilsKt;

import java.nio.charset.StandardCharsets;

public class MailForStorageEnclave extends Enclave {
    byte[] plainText;

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
        // input.getBytes(StandardCharsets.UTF_8)
        // return UtilsKt.toHexString(bytes);
        return bytes;
    }

    @Override
    protected byte[] receiveFromUntrustedHost(byte[] bytes) {
        return encrypt(bytes);
    }

    @Override
    protected void receiveMail(long id, EnclaveMail mail, String routingHint) {
        final byte[] input = mail.getBodyAsBytes();
        final byte[] encrypted = encrypt(input);
        final byte[] responseBytes = postOffice(mail).encryptMail(encrypted);
        postMail(responseBytes, routingHint);
    }
}
