package net.fhirfactory.dricats.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import net.fhirfactory.dricats.internals.common.naming.datatypes.DistinguishedNameEntry;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentMessagingStatistics;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.ui.restclient.MainRESTClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Shared utility for populating common Topology UI components (Tables, Trees) 
 * across different views in the fatclient.
 */
public class TopologyUIUtilities {

    public static ObservableList<KVRow> getDetailsRows(ApplicationComponent summary) {
        ObservableList<KVRow> rows = FXCollections.observableArrayList();
        if (summary == null) {
            rows.add(new KVRow("Info", "No component selected."));
            return rows;
        }
        try { rows.add(new KVRow("Name", Objects.toString(summary.getShortName(), ""))); } catch (Exception ignored) {}
        try {
            if (summary.getIdentifier() != null && summary.getLongName() != null)
                rows.add(new KVRow("LongName", summary.getLongName()));
        } catch (Exception ignored) {}
        try {
            if (summary.getElementInstanceId() != null)
                rows.add(new KVRow("ElementInstanceKey", Objects.toString(summary.resolveElementInstanceKey(), "")));
        } catch (Exception ignored) {}
        try { rows.add(new KVRow("Element Type", Objects.toString(summary.getElementType(), ""))); } catch (Exception ignored) {}
        try { rows.add(new KVRow("Specialization", Objects.toString(summary.getSpecialization(), ""))); } catch (Exception ignored) {}
        try { rows.add(new KVRow("Documentation", Objects.toString(summary.getDocumentation(), ""))); } catch (Exception ignored) {}
        try {
            if (summary.getParent() != null) {
                 String parentName = "";
                 try {
                     parentName = summary.getParent().getElementIdentifier().getIdentifierValue().getUnqualifiedName().getValue();
                 } catch (Exception e) {
                     parentName = summary.getParent().getElementInstanceId().resolveKey();
                 }
                 rows.add(new KVRow("Parent", Objects.toString(parentName, "")));
            }
        } catch (Exception ignored) {}
        try {
            int count = summary.getSubComponents() == null ? 0 : summary.getSubComponents().size();
            rows.add(new KVRow("Subcomponents", Integer.toString(count)));
        } catch (Exception ignored) {}
        try {
            if (summary.getComponentStatus() != null) {
                rows.add(new KVRow("Status", Objects.toString(summary.getComponentStatus().getComponentStatus(), "")));
                String desc = summary.getComponentStatus().getComponentStatusDescription();
                if (desc != null && !desc.isBlank()) rows.add(new KVRow("Status Description", desc));
                if (summary.getComponentStatus().getStartupInstant() != null)
                    rows.add(new KVRow("Startup", Objects.toString(summary.getComponentStatus().getStartupInstant(), "")));
                if (summary.getComponentStatus().getLastActivityInstant() != null)
                    rows.add(new KVRow("Last Activity", Objects.toString(summary.getComponentStatus().getLastActivityInstant(), "")));
                if (summary.getComponentStatus().getLastHeartbeatInstant() != null)
                    rows.add(new KVRow("Last Heartbeat", Objects.toString(summary.getComponentStatus().getLastHeartbeatInstant(), "")));
            }
        } catch (Exception ignored) {}
        return rows;
    }

    public static TreeItem<String> getUniqueNameTreeRoot(ApplicationComponent summary) {
        TreeItem<String> root = new TreeItem<>("UniqueName");
        root.setExpanded(true);
        try {
            if (summary == null || summary.getIdentifier() == null || summary.getIdentifier().getIdentifierValue() == null) {
                root.getChildren().add(new TreeItem<>("No selection"));
            } else {
                Map<Integer, DistinguishedNameEntry> entries =
                        summary.getIdentifier().getIdentifierValue().getUnqualifiedNameEntries();
                if (entries == null || entries.isEmpty()) {
                    root.getChildren().add(new TreeItem<>("<empty>"));
                } else {
                    List<Integer> keys = new ArrayList<>(entries.keySet());
                    Collections.sort(keys);
                    for (Integer k : keys) {
                        DistinguishedNameEntry e = entries.get(k);
                        String qual = e == null ? "" : Objects.toString(e.getQualifier(), "");
                        String val = e == null ? "" : Objects.toString(e.getValue(), "");
                        TreeItem<String> child = new TreeItem<>(qual + " = " + val);
                        root.getChildren().add(child);
                    }
                }
            }
        } catch (Exception ignored) {
            root.getChildren().add(new TreeItem<>("<error reading UniqueName>"));
        }
        return root;
    }

    public static ObservableList<KVRow> getMetricsRows(ApplicationComponent summary, MainRESTClient client) {
        ObservableList<KVRow> rows = FXCollections.observableArrayList();
        if (summary == null) {
            rows.add(new KVRow("Info", "No component selected."));
            return rows;
        }
        String id = MainRESTClient.resolveKey(summary);
        ApplicationComponentMetricsData md = client == null ? null : client.getLatestMetrics(id);
        if (md == null) {
            rows.add(new KVRow("Info", "No metrics available for: " + Objects.toString(summary.getShortName(), id)));
            return rows;
        }
        try { rows.add(new KVRow("Component", Objects.toString(md.getParticipantName(), ""))); } catch (Exception ignored) {}
        try { if (md.getComponentType() != null) rows.add(new KVRow("Type", Objects.toString(md.getComponentType(), ""))); } catch (Exception ignored) {}
        try { if (md.getComponentStartupInstant() != null) rows.add(new KVRow("Startup", Objects.toString(md.getComponentStartupInstant(), ""))); } catch (Exception ignored) {}
        try { if (md.getLastActivityInstant() != null) rows.add(new KVRow("Last Activity", Objects.toString(md.getLastActivityInstant(), ""))); } catch (Exception ignored) {}
        try { if (md.getComponentStatus() != null) rows.add(new KVRow("Status", Objects.toString(md.getComponentStatus(), ""))); } catch (Exception ignored) {}
        ComponentMessagingStatistics ms = md.getMessagingStatistics();
        if (ms != null) {
            rows.add(new KVRow("Messaging", ""));
            try { rows.add(new KVRow("Ingress", Objects.toString(ms.getIngresMessageCount(), "0"))); } catch (Exception ignored) {}
            try { rows.add(new KVRow("Egress Attempts", Objects.toString(ms.getEgressMessageAttemptCount(), "0"))); } catch (Exception ignored) {}
            try { rows.add(new KVRow("Egress Success", Objects.toString(ms.getEgressMessageSuccessCount(), "0"))); } catch (Exception ignored) {}
            try { rows.add(new KVRow("Egress Failures", Objects.toString(ms.getEgressMessageFailureCount(), "0"))); } catch (Exception ignored) {}
            try { rows.add(new KVRow("Internal Sent", Objects.toString(ms.getInternalDistributedMessageCount(), "0"))); } catch (Exception ignored) {}
            try { rows.add(new KVRow("Internal Received", Objects.toString(ms.getInternalReceivedMessageCount(), "0"))); } catch (Exception ignored) {}
        }
        return rows;
    }
}
