/*
 * Copyright (C) 2020 Dominik Schadow, dominikschadow@gmail.com
 *
 * This file is part of the Java Security project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dominikschadow.javasecurity.tink.mac;

import com.google.crypto.tink.*;
import com.google.crypto.tink.mac.MacConfig;
import com.google.crypto.tink.mac.MacKeyTemplates;
import de.dominikschadow.javasecurity.tink.TinkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Shows crypto usage with Google Tink for the Hash-based Message Authentication Code (HMAC) primitive. The used key is
 * stored and loaded from the project. Selected algorithm is SHA 256 with 128 bit.
 *
 * @author Dominik Schadow
 */
public class HmcShaWithSavedKey {
    private static final Logger log = LoggerFactory.getLogger(HmcShaWithSavedKey.class);
    private static final String INITIAL_TEXT = "Some dummy text to work with";
    private static final String KEYSET_FILENAME = "crypto-tink/src/main/resources/keysets/hmac-sha.json";

    /**
     * Init MacConfig in the Tink library.
     */
    private HmcShaWithSavedKey() {
        try {
            MacConfig.register();
        } catch (GeneralSecurityException ex) {
            log.error("Failed to initialize Tink", ex);
        }
    }

    public static void main(String[] args) {
        HmcShaWithSavedKey demo = new HmcShaWithSavedKey();

        try {
            demo.generateAndStoreKey();

            KeysetHandle keysetHandle = demo.loadKey();

            byte[] tag = demo.computeMac(keysetHandle);
            boolean valid = demo.verifyMac(keysetHandle, tag);

            TinkUtils.printMacData(keysetHandle, INITIAL_TEXT, tag, valid);
        } catch (GeneralSecurityException ex) {
            log.error("Failure during Tink usage", ex);
        } catch (IOException ex) {
            log.error("Failure during storing key", ex);
        }
    }

    /**
     * Stores the keyset in the projects resources/keysets directory if it does not exist yet.
     *
     * @throws IOException              Failure during saving
     * @throws GeneralSecurityException Failure during keyset generation
     */
    private void generateAndStoreKey() throws IOException, GeneralSecurityException {
        File keysetFile = new File(KEYSET_FILENAME);

        if (!keysetFile.exists()) {
            KeysetHandle keysetHandle = KeysetHandle.generateNew(MacKeyTemplates.HMAC_SHA256_128BITTAG);
            CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(keysetFile));
        }
    }

    private KeysetHandle loadKey() throws IOException, GeneralSecurityException {
        return CleartextKeysetHandle.read(JsonKeysetReader.withFile(new File(KEYSET_FILENAME)));
    }

    private byte[] computeMac(KeysetHandle keysetHandle) throws GeneralSecurityException {
        Mac mac = keysetHandle.getPrimitive(Mac.class);

        return mac.computeMac(INITIAL_TEXT.getBytes());
    }

    private boolean verifyMac(KeysetHandle keysetHandle, byte[] tag) {
        try {
            Mac mac = keysetHandle.getPrimitive(Mac.class);
            mac.verifyMac(tag, INITIAL_TEXT.getBytes());
            return true;
        } catch (GeneralSecurityException ex) {
            log.error("MAC is invalid", ex);
        }

        return false;
    }
}