package com.r3.conclave.sample.encryptHost;

import com.r3.conclave.common.EnclaveInstanceInfo;
import com.r3.conclave.host.AttestationParameters;
import com.r3.conclave.host.EnclaveHost;
import com.r3.conclave.host.EnclaveLoadException;
import com.r3.conclave.host.MailCommand;
import com.r3.conclave.host.MockOnlySupportedException;
import com.r3.conclave.utilities.internal.UtilsKt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * This class demonstrates how to load an enclave and exchange byte arrays with it.
 */
public class Host {
    private static void doEncyptFLow(String input) throws IOException, EnclaveLoadException {
        EnclaveHost enclave = EnclaveHost.load("com.r3.conclave.sample.enclave.MailForStorageEnclave");
        enclave.start(new AttestationParameters.DCAP(), (commands) -> {
            // ignore
        });

        // The attestation data must be provided to the client of the enclave, via whatever mechanism you like.
        final EnclaveInstanceInfo attestation = enclave.getEnclaveInstanceInfo();
        final byte[] attestationBytes = attestation.serialize();

        // It has a useful toString method.
        System.out.println(EnclaveInstanceInfo.deserialize(attestationBytes));

        final Charset utf8 = StandardCharsets.UTF_8;
        byte[] encrypted = enclave.callEnclave(input.getBytes(utf8));

        System.out.println();
        System.out.println("Encrypting " + input +": " + UtilsKt.toHexString(encrypted));
        System.out.println();

        enclave.close();
    }

    private static void reportPlatformSupport() {
        // Report whether the platform supports hardware enclaves.
        //
        // This method will always check the hardware state regardless of whether running in Simulation,
        // Debug or Release mode. If the platform supports hardware enclaves then no exception is thrown.
        // If the platform does not support enclaves or requires enabling, an exception is thrown with the
        // details in the exception message.
        //
        // If the platform supports enabling of enclave support via software then passing true as a parameter
        // to this function will attempt to enable enclave support on the platform. Normally this process
        // will have to be run with root/admin privileges in order for it to be enabled successfully.
        try {
            EnclaveHost.checkPlatformSupportsEnclaves(true);
            System.out.println("This platform supports enclaves in simulation, debug and release mode.");
        } catch (MockOnlySupportedException e) {
            System.out.println("This platform only supports mock enclaves: " + e.getMessage());
            System.exit(1);
        } catch (EnclaveLoadException e) {
            System.out.println("This platform does not support hardware enclaves: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws EnclaveLoadException, IOException {

        if (args.length == 0) {
            System.err.println("Please pass the string to encypt on the command line using --args=\"String to encrypt\"");
            return;
        }

        String plainString = String.join(" ", args);

        reportPlatformSupport();
        doEncyptFLow(plainString);
    }

}
