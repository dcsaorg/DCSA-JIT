package org.dcsa.jit.util;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.TimestampDefinition;
import org.dcsa.core.events.util.ExtendedGenericEventRequest;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.query.DBEntityAnalysis;
import org.dcsa.jit.model.OpsEventTimestampDefinition;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.relational.core.sql.Join;

import java.util.Set;

public class ExtendedOperationsEventRequest extends ExtendedGenericEventRequest {

    private static final Set<Class<? extends Event>> OPS_EVENT_CLASS = Set.of(OperationsEvent.class);

    public ExtendedOperationsEventRequest(ExtendedParameters extendedParameters, R2dbcDialect r2dbcDialect) {
        super(extendedParameters, r2dbcDialect, OPS_EVENT_CLASS);
    }

    @Override
    protected DBEntityAnalysis.DBEntityAnalysisBuilder<Event> prepareDBEntityAnalysis() {
        DBEntityAnalysis.DBEntityAnalysisBuilder<Event> builder = super.prepareDBEntityAnalysis();

        return builder
                .join(Join.JoinType.JOIN, builder.getPrimaryModelClass(), OpsEventTimestampDefinition.class)
                .onFieldEqualsThen("eventID", "eventID")
                .chainJoin(TimestampDefinition.class)
                .onFieldEqualsThen("timestampDefinition", "id")
                .registerQueryFieldFromField("negotiationCycle");
    }
}
