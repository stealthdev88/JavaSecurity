/*
 * Copyright (C) 2016 Dominik Schadow, dominikschadow@gmail.com
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
package de.dominikschadow.javasecurity.asymmetric;

import org.keyczar.Signer;
import org.keyczar.Verifier;
import org.keyczar.exceptions.KeyczarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Digital signature sample with Keyczar. Loads the DSA key from the sample key set, signs and verifies sample text with it.
 *
 * @author Dominik Schadow
 */
public class DSASignature {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSASignature.class);
    private static final String KEYSET_PATH = "crypto-keyczar/src/main/resources/key-sets/sign";

    public static void main(String[] args) {
        DSASignature res = new DSASignature();
        final String initialText = "Some dummy text to sign";
        try {
            String signature = res.sign(initialText);
            boolean valid = res.verify(initialText, signature);

            res.printReadableMessages(initialText, signature, valid);
        } catch (KeyczarException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private String sign(String initialText) throws KeyczarException {
        Signer signer = new Signer(KEYSET_PATH);
        return signer.sign(initialText);
    }

    private boolean verify(String initialText, String signature) throws KeyczarException {
        Verifier verifier = new Verifier(KEYSET_PATH);
        return verifier.verify(initialText, signature);
    }

    private void printReadableMessages(String initialText, String signature, boolean valid) {
        LOGGER.info("initialText: {}", initialText);
        LOGGER.info("signature as Base64: {}", signature);
        LOGGER.info("signature valid: {}", valid);
    }
}
