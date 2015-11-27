/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.licensing;

/**
 *
 * @author DauBufu
 */


import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrialKeyGenerator extends Application {

    
    private static final int ITERATIONS_ENCRYPT = 10000;
    private static final String SECRET_KEY_FACTORY = "PBKDF2WithHmacSHA1";
    private static final String CIPHER = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM = "AES";
    private static final int KEY_LENGTH = 128;
    private static final String PASS_ENCRYPT = "777DAUBUFU$$$";
    private static final String SALT_ENCRYPT = "RO.SSC.SPT";

    private static final Logger log = LoggerFactory.getLogger(TrialKeyGenerator.class);
    private static final String ROOT_LAYOUT_FILE = "/fxml/RootLayout_1.fxml";
    private static final String MAIN_CSS_FILE = "/styles/Main.css";
    private static final double SCENE_MIN_WIDTH = 600;
    private static final double SCENE_MIN_HEIGHT = 400;
    private Stage stage;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        log.info("Starting Litho InSight");
        this.stage = stage;

        log.debug("Loading FXML for main view from: {}", ROOT_LAYOUT_FILE);
        FXMLLoader loader = new FXMLLoader();

        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(ROOT_LAYOUT_FILE));

        log.debug("Showing JFX scene");
        Scene scene = new Scene(rootNode, SCENE_MIN_WIDTH, SCENE_MIN_HEIGHT);
        // scene.getStylesheets().add(MAIN_CSS_FILE);
       
        stage.setTitle("Soft Pontaj v2.0");
        stage.setMinWidth(SCENE_MIN_WIDTH);
        stage.setMinHeight(SCENE_MIN_HEIGHT);
        stage.setScene(scene);

        stage.show();
    }

    public Stage getStage() {
        return stage;
    }




    public static String generateKey(String toEncode) {

        String encoded = "";
        try {
            byte[] saltEncrypt = SALT_ENCRYPT.getBytes();
            SecretKeyFactory factoryKeyEncrypt = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY);
            SecretKey tmp = factoryKeyEncrypt.generateSecret(new PBEKeySpec(PASS_ENCRYPT.toCharArray(), saltEncrypt, ITERATIONS_ENCRYPT, KEY_LENGTH));
            SecretKeySpec encryptKey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
            Cipher aesCipherEncrypt = Cipher.getInstance(CIPHER);
            aesCipherEncrypt.init(Cipher.ENCRYPT_MODE, encryptKey);
            byte[] bytes = StringUtils.getBytesUtf8(toEncode);
            byte[] encryptBytes = aesCipherEncrypt.doFinal(bytes);
            encoded = Base64.encodeBase64URLSafeString(encryptBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encoded;
    }

   
}
