package com.wxshop.shop.service;

import com.wxshop.shop.controller.AuthController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class TelVerificationServiceTest {
    public static AuthController.TelAndCode VALID_PARAMETER = new AuthController.TelAndCode("13811111111", null);
    public static AuthController.TelAndCode VALID_PARAMETER_CODE = new AuthController.TelAndCode("13811111111", "000000");
    public static AuthController.TelAndCode EMPTY_TEL = new AuthController.TelAndCode(null, null);
    public static AuthController.TelAndCode EMPTY_PARAMETER = null;

    @Test
    void returnTrueIfValid() {
        Assertions.assertTrue(
                new TelVerificationService().verifyTelParameters(VALID_PARAMETER));
    }

    @Test
    void returnFalseIfNoTel() {
        Assertions.assertFalse(new TelVerificationService().verifyTelParameters(EMPTY_TEL));
    }

    @Test
    void returnFalseIfNoParam() {
        Assertions.assertFalse(new TelVerificationService().verifyTelParameters(EMPTY_PARAMETER));
    }

}
