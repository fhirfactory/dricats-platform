/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.internals.datatypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class UserLogin implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Member Variables
    //

    private String loginName;
    private String system;
    private EffectiveDate validPeriod;

    //
    // Constructor(s)
    //

    public UserLogin() {
        super();
        validPeriod = new EffectiveDate();
    }

    //
    // Accessors and Mutators
    //

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public EffectiveDate getValidPeriod() {
        return validPeriod;
    }

    public void setValidPeriod(EffectiveDate validPeriod) {
        this.validPeriod = validPeriod;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", UserLogin.class.getSimpleName() + "[", "]")
                .add("loginName='" + getLoginName() + "'")
                .add("system='" + getSystem() + "'")
                .add("validPeriod=" + getValidPeriod())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserLogin userLogin = (UserLogin) o;
        return Objects.equals(getLoginName(), userLogin.getLoginName()) && Objects.equals(getSystem(), userLogin.getSystem()) && Objects.equals(getValidPeriod(), userLogin.getValidPeriod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLoginName(), getSystem(), getValidPeriod());
    }
}
