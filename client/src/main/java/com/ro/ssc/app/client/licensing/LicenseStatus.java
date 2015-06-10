/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.licensing;

import java.util.Date;

/**
 *
 * @author DauBufu
 */
public class LicenseStatus {

    private final Date expireDate;
    private final boolean expired;

    public LicenseStatus(Date expireDate, boolean expired) {
        this.expireDate = expireDate;
        this.expired = expired;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public boolean isExpired() {
        return expired;
    }

}
