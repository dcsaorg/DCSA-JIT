package org.dcsa.jit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.validator.ValidVesselIMONumber;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Pattern;
import java.util.UUID;

@Table("transport_call_subscription")
@Data
@NoArgsConstructor
public class TransportCallSubscription extends AuditBase {

    @Id
    @JsonProperty("subscriptionID")
    @Column("subscription_id")
    private UUID id;

    @JsonProperty("callbackUrl")
    @Column("callback_url")
    private String callbackUrl;

    @JsonProperty("carrierVoyageNumber")
    @Column("carrier_voyage_number")
    private String carrierVoyageNumber;

    @JsonProperty("vesselIMONumber")
    @Column("vessel_imo_number")
    @Pattern(regexp = "[0-9]{7}")
    @ValidVesselIMONumber
    private String vesselIMONumber;

    @JsonProperty("unLocationCode")
    @Column("un_location_code")
    private String unLocationCode;

}

