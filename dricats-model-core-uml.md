### UML Model for `dricats-model-core` Classes

This document describes the core class hierarchy within the `dricats-model-core` module and provides a Draw.io CSV Import block to visualize it.

#### Key Class Hierarchy
*   **SerialisableObject** (Root Object)
    *   **ManagedObject** (Common metadata/identifiers)
        *   **ElementBase** (Archimate element base)
            *   **ApplicationComponent** (Major functional unit)
                *   **TopologyComponent**
                    *   **Subsystem**
            *   **ApplicationService** (Exposed behavior)
            *   **ApplicationFunction** (Internal behavior)
            *   **ApplicationInterface** (Point of access)
            *   **ApplicationDataObject** (Information/Data)
        *   **RelationshipBase** (Archimate relationship base)
            *   **CompositionRelationship**
            *   **AggregationRelationshipBase**
            *   **AssociationRelationshipBase**
            *   **FlowRelationship**
    *   **Topic** (Messaging/Data topic definition)

#### Major Associations
*   `ApplicationComponent` aggregates `ApplicationFunction`, `ApplicationService`, and `ApplicationInterface`.
*   `ApplicationService` is owned by `ApplicationComponent` and exposes `ApplicationInterface`s.
*   `ApplicationFunction` is owned by `ApplicationComponent` and realizes `ApplicationService`s.
*   `ApplicationDataObject` is accessed by `ApplicationFunction`s and `ApplicationService`s, and is linked to a `Topic`.

#### Draw.io CSV Import Data
To visualize the UML model:
1.  Open [draw.io](https://app.diagrams.net/).
2.  Go to **Arrange** > **Insert** > **Advanced** > **CSV...**.
3.  Paste the following block into the dialog:

```csv
# label: %name%
# style: shape=umlEntity;fillColor=#ffffff;strokeColor=#000000;verticalAlign=top;align=center;spacingTop=25;fontStyle=1
# parentstyle: shape=folder;fontStyle=1;spacingTop=10;fillColor=#dae8fc;strokeColor=#6c8ebf
# identity: id
# connection: { "from": "parentId", "to": "id", "invert": true, "label": "extends", "style": "endArrow=block;endSize=12;endFill=0;dashed=0;verticalAlign=bottom;" }
# connection: { "from": "ownerId", "to": "id", "label": "owner", "style": "endArrow=open;endSize=12;dashed=1;" }
# width: 220
# height: 80
# padding: 15
# nodespacing: 40
# levelspacing: 80
# layout: horizontalflow
# ----
id,name,parentId,ownerId,style
SO,SerialisableObject,,,shape=umlEntity;fillColor=#f5f5f5
MO,ManagedObject,SO,,shape=umlEntity;fillColor=#f5f5f5
EB,ElementBase,MO,,shape=umlEntity;fillColor=#d5e8d4;strokeColor=#82b366
RB,RelationshipBase,MO,,shape=umlEntity;fillColor=#fff2cc;strokeColor=#d6b656
AC,ApplicationComponent,EB,,shape=umlEntity;fillColor=#dae8fc;strokeColor=#6c8ebf
AS,ApplicationService,EB,AC,shape=umlEntity;fillColor=#dae8fc;strokeColor=#6c8ebf
AF,ApplicationFunction,EB,AC,shape=umlEntity;fillColor=#dae8fc;strokeColor=#6c8ebf
AI,ApplicationInterface,EB,AC,shape=umlEntity;fillColor=#dae8fc;strokeColor=#6c8ebf
ADO,ApplicationDataObject,EB,,shape=umlEntity;fillColor=#dae8fc;strokeColor=#6c8ebf
TC,TopologyComponent,AC,,shape=umlEntity;fillColor=#e1d5e7;strokeColor=#9673a6
SS,Subsystem,TC,,shape=umlEntity;fillColor=#e1d5e7;strokeColor=#9673a6
CR,CompositionRelationship,RB,,shape=umlEntity;fillColor=#fff2cc;strokeColor=#d6b656
AR,AggregationRelationshipBase,RB,,shape=umlEntity;fillColor=#fff2cc;strokeColor=#d6b656
FR,FlowRelationship,RB,,shape=umlEntity;fillColor=#fff2cc;strokeColor=#d6b656
T,Topic,SO,,shape=umlEntity;fillColor=#f8cecc;strokeColor=#b85450
```
