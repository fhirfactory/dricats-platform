/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package net.fhirfactory.dricats.platform.middleware.jgroups;

import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JGroupTransactionResultEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The JGroupsTransactionResult is returned as part of a JGroups transaction (message send or RPC activity). It
 * contains a resultMessage (free text description or information), a resultCode (an enum formalising the result
 * condition) and a timestamp.
 */
public class JGroupsTransactionResult implements Serializable {

    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 3403310128066773387L;

    //
    // Attributes
    //

    /**
     * The resultCode is a formalised definition of the result (i.e. OK, Failed, Unknown).
     */
    private JGroupTransactionResultEnum resultCode;
    /**
     * The resultMessage is a free-form text description of the result, used to provide additional information
     * on the state of the result
     */
    private String resultMessage;
    /**
     * The timestamp represents the time, at the receiving endpoint, at which the message was received (if the target
     * endpoint actually received it) or the time at which a failure condition was identified.
     */
    private LocalDateTime timestamp;

    //
    // Bean Methods
    //

    public JGroupTransactionResultEnum getResultCode() {
        return resultCode;
    }

    public void setResultCode(JGroupTransactionResultEnum resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
