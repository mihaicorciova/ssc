package com.ro.ssc.app.client.licensing;

import com.ro.ssc.app.client.model.commons.Configuration;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrialKeyValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrialKeyValidator.class);

    private static final int ITERATIONS_DECRYPT = 10000;
    private static final int KEY_LENGTH = 128;
    private static final String SECRET_KEY_FACTORY = "PBKDF2WithHmacSHA1";
    private static final String CIPHER = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM = "AES";
    private static final String PASS_DECRYPT = "777DAUBUFU$$$";
    private static final String SALT_DECRYPT = "RO.SSC.SPT";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("dd-MM-yyyy");

    public String decodeKey(String encodedEncrypted) {
        String decoded = "";
        try {
            byte[] saltDecrypt = SALT_DECRYPT.getBytes(StandardCharsets.UTF_8);
            SecretKeyFactory factoryKeyDecrypt = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY);
            SecretKey tmp2 = factoryKeyDecrypt.generateSecret(new PBEKeySpec(PASS_DECRYPT.toCharArray(), saltDecrypt, ITERATIONS_DECRYPT, KEY_LENGTH));
            SecretKeySpec decryptKey = new SecretKeySpec(tmp2.getEncoded(), ALGORITHM);
            Cipher aesCipherDecrypt = Cipher.getInstance(CIPHER);
            aesCipherDecrypt.init(Cipher.DECRYPT_MODE, decryptKey);
            byte[] e64bytes = StringUtils.getBytesUtf8(encodedEncrypted);
            byte[] eBytes = Base64.decodeBase64(e64bytes);
            byte[] cipherDecode = aesCipherDecrypt.doFinal(eBytes);
            decoded = StringUtils.newStringUtf8(cipherDecode);
        } catch (Exception e) {
            LOGGER.error("Error while decoding the trial key", e);
        }
        return decoded;
    }

    public LicenseStatus getLicenseStatus() {
        try {
            DateTime expireDate = getExpireDate();
            return new LicenseStatus(expireDate.toDate(), expireDate.isBeforeNow());
        } catch (Exception e) {
            LOGGER.error("Error while validating the license key", e);
        }
        return new LicenseStatus(null, true);
    }

    private DateTime getExpireDate() {
        return DATE_FORMAT.parseDateTime(decodeKey(Configuration.TRIAL_KEY.getAsString()));
    }

}
