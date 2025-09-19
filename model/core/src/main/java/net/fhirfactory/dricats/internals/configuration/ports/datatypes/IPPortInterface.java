/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.internals.configuration.ports.datatypes;

import java.io.Serial;
import java.io.Serializable;

public class IPPortInterface implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private String ipAddress;
    private int port;
    private String protocol;

    //
    // Constructor(s)
    //
    public IPPortInterface() {
        super();
    }

    public IPPortInterface(String ipAddress, int port){
        super();
        this.ipAddress = ipAddress;
        this.port = port;
    }

    //
    // Accessors
    //
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    //
    // Standard Methods
    //
    @Override
    public String toString() {
        return "IPPortInterface [ipAddress=" + ipAddress + ", port=" + port + ", protocol=" + protocol + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPPortInterface that = (IPPortInterface) o;
        if (port != that.port) return false;
        if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null) return false;
        return protocol != null ? protocol.equals(that.protocol) : that.protocol == null;
    }

    @Override
    public int hashCode() {
        int result = ipAddress != null ? ipAddress.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
        return result;
    }
}
