package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.model.GetId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.UUID;

@Table("schedule_subscription")
@Data
@NoArgsConstructor
public class ScheduleSubscription extends AuditBase implements GetId<UUID> {

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

    @JsonProperty("unLocationCode")
    @Column("un_location_code")
    private String unLocationCode;

    @JsonProperty("vesselIMONumber")
    @Column("vessel_imo_number")
    private Integer vesselIMONumber;

    @JsonProperty("startDate")
    @Column("start_date")
    private OffsetDateTime startDate;

    @JsonProperty("carrierServiceCode")
    @Column("carrier_service_code")
    private String carrierServiceCode;

    @Pattern(regexp = "^(P(\\dY)?(\\dM)?(\\dD)?)?(T(\\dH)?(\\dM)?(\\dS)?)?$")
    @Column("date_range")
    private String dateRange;

}
