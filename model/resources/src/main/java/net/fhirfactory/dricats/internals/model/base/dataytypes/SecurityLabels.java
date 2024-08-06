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
package net.fhirfactory.dricats.internals.model.base.dataytypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SecurityLabels implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900394L;
    private static final Logger LOG = LoggerFactory.getLogger(SecurityLabels.class);

    //
    // Attributes
    //

    private CodeableConcept confidentialityClassification;
    private List<CodeableConcept> sensitivityClassifications;
    private List<CodeableConcept> purposeOfUseClassifications;
    private List<CodeableConcept> restrictionClassifications;
    private List<CodeableConcept> restrictionOfUseClassifications;
    private List<CodeableConcept> informationCompartments;

    //
    // Constructor(s)
    //

    public SecurityLabels(){
        sensitivityClassifications = new ArrayList<>();
        purposeOfUseClassifications = new ArrayList<>();
        restrictionClassifications = new ArrayList<>();
        restrictionOfUseClassifications = new ArrayList<>();
        informationCompartments = new ArrayList<>();
    }

    //
    // Bean Methods
    //

    public CodeableConcept getConfidentialityClassification() {
        return confidentialityClassification;
    }

    public void setConfidentialityClassification(CodeableConcept confidentialityClassification) {
        this.confidentialityClassification = confidentialityClassification;
    }

    public List<CodeableConcept> getSensitivityClassifications() {
        return sensitivityClassifications;
    }

    public void setSensitivityClassifications(List<CodeableConcept> sensitivityClassifications) {
        this.sensitivityClassifications = sensitivityClassifications;
    }

    public List<CodeableConcept> getPurposeOfUseClassifications() {
        return purposeOfUseClassifications;
    }

    public void setPurposeOfUseClassifications(List<CodeableConcept> purposeOfUseClassifications) {
        this.purposeOfUseClassifications = purposeOfUseClassifications;
    }

    public List<CodeableConcept> getRestrictionClassifications() {
        return restrictionClassifications;
    }

    public void setRestrictionClassifications(List<CodeableConcept> restrictionClassifications) {
        this.restrictionClassifications = restrictionClassifications;
    }

    public List<CodeableConcept> getRestrictionOfUseClassifications() {
        return restrictionOfUseClassifications;
    }

    public void setRestrictionOfUseClassifications(List<CodeableConcept> restrictionOfUseClassifications) {
        this.restrictionOfUseClassifications = restrictionOfUseClassifications;
    }

    public List<CodeableConcept> getInformationCompartments() {
        return informationCompartments;
    }

    public void setInformationCompartments(List<CodeableConcept> informationCompartments) {
        this.informationCompartments = informationCompartments;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return LOG;
    }
}
